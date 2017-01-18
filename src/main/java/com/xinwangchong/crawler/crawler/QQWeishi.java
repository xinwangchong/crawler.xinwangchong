package com.xinwangchong.crawler.crawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.alibaba.fastjson.JSON;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xinwangchong.crawler.common.tools.ResourceUtils;
import com.xinwangchong.crawler.common.tools.Constant;
import com.xinwangchong.crawler.common.tools.DateUtils;
import com.xinwangchong.crawler.common.tools.HttpClient;
import com.xinwangchong.crawler.common.tools.JsoupUtils;
import com.xinwangchong.crawler.common.tools.StringUtils;
import com.xinwangchong.crawler.entity.CrawlerVideo;
import com.xinwangchong.crawler.service.ResourceService;

public class QQWeishi implements CrawlerData {
	public static Logger log = Logger.getLogger(QQWeishi.class);

	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		// TODO Auto-generated method stub
		WebClient wc = new WebClient(BrowserVersion.FIREFOX_45);
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.NoOpLog");

		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);

		java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
		wc.setJavaScriptTimeout(5000);
		wc.getOptions().setUseInsecureSSL(true);// 接受任何主机连接 无论是否有有效证书
		wc.getOptions().setJavaScriptEnabled(true);// 设置支持javascript脚本
		wc.getOptions().setCssEnabled(false);// 禁用css支持
		wc.getOptions().setThrowExceptionOnScriptError(false);// js运行错误时不抛出异常
		wc.getOptions().setTimeout(100000);// 设置连接超时时间
		wc.getOptions().setDoNotTrackEnabled(false);
		HtmlPage page = wc.getPage("http://www.weishi.com/c/1");
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String res = page.asXml();
		System.out.println(res);
	}

	public static Map<String, Object> crawlerFirstPage(String type, ResourceService resourceService) {
		Map<String, Object> result = new HashMap<String, Object>();
		String url = "http://weishi.qq.com/c/" + type + "?fall=1";
		Document doc = JsoupUtils.jsoupConnGet(url, null, null, 0);
		if (doc == null) {
			for (int i = 0; i < 60; i++) {
				doc = JsoupUtils.jsoupConnGet(url, null, null, 0);
				if (doc != null) {
					break;
				}
			}
			if (doc == null) {
				return null;
			}
		}
		Elements els = doc.select("article");
		String id = null;
		CrawlerVideo cv = null;
		for (Element e : els) {
			cv = new CrawlerVideo();
			cv.setId(StringUtils.getUUID());
			cv.setImgUrl(e.select("img").attr("src"));
			Elements video_container = e.select("div.content-l-video.pr.cp");
			String vu = video_container.attr("data-video");
			cv.setVideoUrl(vu);
			cv.setTitle(StringUtils.getChineseInString(e.select("div.content-l-video.pr.cp>a.content-l-p.pa").text()));
			id = video_container.attr("data-id");
			cv.setType(ResourceUtils.getTypeName(type));
			cv.setSource(Constant.MEI_PAI);
			/*
			 * try { resourceService.addCrawlerVideosingle(cv); } catch
			 * (Exception e1) { log.info(DateUtils.dateToString(new Date())+"  "
			 * +vu+" 视频资源入库失败"+" 异常信息："+e1.getMessage()); }
			 */
		}
		if (id != null && !id.equals("")) {
			result.put("maxid", id);
			return result;
		}
		return null;
	}

	public static Map<String, Object> crawlerAjaxPage_1(int page, String type, ResourceService resourceService) {
		String url = "http://www.meipai.com/squares/new_timeline?page=" + page + "&count=24&tid=" + type;
		String str = HttpClient.get(url, null, 0);
		if (str == null) {
			for (int i = 0; i < 60; i++) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					log.info(DateUtils.dateToString(new Date()) + "  HttpClient -> get 爬取美拍异步分页数据失败 页码：" + page
							+ " 视频资源类型 ：" + type + " 异常信息：" + e.getMessage());
				}
				str = HttpClient.get(url, null, 0);
				if (str != null) {
					break;
				}
			}
			if (str == null) {
				return null;
			}
		}
		Map<String, Object> re = (Map<String, Object>) JSON.parse(str);
		List<Map<String, Object>> data = (List<Map<String, Object>>) JSON.parse(re.get("medias").toString());
		CrawlerVideo cv = null;
		for (Map<String, Object> map : data) {
			cv = new CrawlerVideo();
			cv.setId(StringUtils.getUUID());
			cv.setImgUrl(map.get("cover_pic").toString());
			cv.setTitle(StringUtils.getChineseInString(map.get("caption").toString()));
			String vu = map.get("video").toString();
			cv.setVideoUrl(map.get("video").toString());
			cv.setType(ResourceUtils.getTypeName(type));
			cv.setSource(Constant.MEI_PAI);
			try {
				resourceService.addCrawlerVideosingle(cv);
			} catch (Exception e) {
				log.info(DateUtils.dateToString(new Date()) + "  " + vu + " 视频资源入库失败" + " 异常信息：" + e.getMessage());
			}
		}
		return null;
	}

	public static Map<String, Object> crawlerAjaxPage_2(int page, String type, String maxid,
			ResourceService resourceService) {
		String url = "http://www.meipai.com/topics/hot_timeline?page=" + page + "&count=24&tid=" + type + "&maxid="
				+ maxid;
		String str = HttpClient.get(url, null, 0);
		if (str == null) {
			for (int i = 0; i < 60; i++) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					log.info(DateUtils.dateToString(new Date()) + "  HttpClient -> get 爬取美拍异步分页数据失败 页码：" + page
							+ " 视频资源类型 ：" + type + " 异常信息：" + e.getMessage());
				}
				str = HttpClient.get(url, null, 0);
				if (str != null) {
					break;
				}
			}
			if (str == null) {
				return null;
			}
		}
		Map<String, Object> re = (Map<String, Object>) JSON.parse(str);
		List<Map<String, Object>> data = (List<Map<String, Object>>) JSON.parse(re.get("medias").toString());
		Map<String, Object> result = new HashMap<String, Object>();
		CrawlerVideo cv = null;
		String id = null;
		for (Map<String, Object> map : data) {
			cv = new CrawlerVideo();
			cv.setId(StringUtils.getUUID());
			cv.setImgUrl(map.get("cover_pic").toString());
			cv.setTitle(StringUtils.getChineseInString(map.get("caption").toString()));
			String vu = map.get("video").toString();
			cv.setVideoUrl(map.get("video").toString());
			cv.setType(ResourceUtils.getTypeName(type));
			id = map.get("id").toString();
			cv.setSource(Constant.MEI_PAI);
			try {
				resourceService.addCrawlerVideosingle(cv);
			} catch (Exception e) {
				log.info(DateUtils.dateToString(new Date()) + "  " + vu + " 视频资源入库失败" + " 异常信息：" + e.getMessage());
			}
		}
		if (id != null && !id.equals("")) {
			result.put("maxid", id);
			return result;
		}
		return null;
	}

	/*
	 * 13.搞笑16.明星19.女神63.舞蹈27.美妆31.男神18.宝宝 59.美食(tid=5870490265939297486)
	 * 62.音乐(tid=5871155236525660080) 63.舞蹈(tid=5872239354896137479)
	 * 6.宠物(tid=5864549574576746574) 423.吃秀(tid=6161763227134314911)
	 * 450.手工(tid=5875185672678760586)
	 **/
	public Map<String, Object> crawler(ResourceService resourceService, int pages) {
		String[] types_1 = { "13", "16", "19", "27", "31", "18" };
		String[] types_2 = { "59-5870490265939297486", "62-5871155236525660080", "63-5872239354896137479",
				"6-5864549574576746574", "423-6161763227134314911", "450-5875185672678760586" };
		String maxid = "";
		for (String type : types_1) {
			for (int i = 1; i <= pages; i++) {
				if (i > 1) {
					crawlerAjaxPage_1(i, type, resourceService);

				} else {
					crawlerFirstPage(type, resourceService);
				}
			}
		}
		for (String type : types_2) {
			String[] split = type.split("-");
			for (int i = 1; i <= pages; i++) {
				Map<String, Object> re = null;
				if (i > 1) {
					re = crawlerAjaxPage_2(i, split[1], maxid, resourceService);
				} else {
					re = crawlerFirstPage(split[0], resourceService);
				}
				if (re != null) {
					maxid = (String) re.get("maxid");
				}
			}
		}
		return null;
	}
}

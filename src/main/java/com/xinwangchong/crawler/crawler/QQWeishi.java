package com.xinwangchong.crawler.crawler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.alibaba.fastjson.JSON;
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
	public static void main(String[] args) {
		String url="http://wsm.qq.com/weishi/tag/channelTimeline.php?v=p&g_tk=1184065862&r=1484635017429&callback=jQuery110205478808217984563_1484635017155&start=0&pageflag=2&reqnum=12&lastid=&type=1&key=1&_=1484635017156";
		String cookie="pt2gguin=o1083447590; ptcz=cf88c8b9c1c2ef2341d49391145168d0a974217d374a7c30afa119e2e265c36b; uin=o1083447590; skey=@RHm7EI7SK; pgv_pvi=7413371904; pgv_si=s2159676416; ptisp=ctc; RK=ovmOheiqcp; rv2=8055D3DE30FB892AB074F4E943F1C24866DE9658BDB8B14780; property20=AE56C1F5A5F85CB2D086EB37C703C60890F2E6114809556EC903CA38127B35585CFD1B3C6F9E48CE; pgv_pvid=7055602006; pgv_info=ssid=s1246900245; o_cookie=1083447590";
		String string = "{\"ret\":-4,\"errcode\":-4,\"msg\":\"\u8df3\u8f6c\u53c2\u6570\u9519\u8bef\"}";
		System.out.println(JSON.parse(string));
	}
	public static Map<String, Object> crawlerFirstPage(String type, ResourceService resourceService) {
		Map<String, Object> result = new HashMap<String, Object>();
		String url = "http://weishi.qq.com/c/"+type+"?fall=1";
		Document doc = JsoupUtils.jsoupConn(url, null);
		if (doc == null) {
			for (int i = 0; i < 60; i++) {
				doc = JsoupUtils.jsoupConn(url, null);
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
			/*try {
				resourceService.addCrawlerVideosingle(cv);
			} catch (Exception e1) {
				log.info(DateUtils.dateToString(new Date())+"  "+vu+" 视频资源入库失败"+" 异常信息："+e1.getMessage());
			}*/
		}
		if (id != null && !id.equals("")) {
			result.put("maxid", id);
			return result;
		}
		return null;
	}

	public static Map<String, Object> crawlerAjaxPage_1(int page, String type, ResourceService resourceService) {
		String url = "http://www.meipai.com/squares/new_timeline?page=" + page + "&count=24&tid=" + type;
		String str = HttpClient.get(url, null);
		if (str == null) {
			for (int i = 0; i < 60; i++) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					log.info(DateUtils.dateToString(new Date())+"  HttpClient -> get 爬取美拍异步分页数据失败 页码："+page+" 视频资源类型 ："+type+" 异常信息："+e.getMessage());
				}
				str = HttpClient.get(url, null);
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
				log.info(DateUtils.dateToString(new Date())+"  "+vu+" 视频资源入库失败"+" 异常信息："+e.getMessage());
			}
		}
		return null;
	}

	public static Map<String, Object> crawlerAjaxPage_2(int page, String type, String maxid,
			ResourceService resourceService) {
		String url = "http://www.meipai.com/topics/hot_timeline?page=" + page + "&count=24&tid=" + type + "&maxid="
				+ maxid;
		String str = HttpClient.get(url, null);
		if (str == null) {
			for (int i = 0; i < 60; i++) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					log.info(DateUtils.dateToString(new Date())+"  HttpClient -> get 爬取美拍异步分页数据失败 页码："+page+" 视频资源类型 ："+type+" 异常信息："+e.getMessage());
				}
				str = HttpClient.get(url, null);
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
				log.info(DateUtils.dateToString(new Date())+"  "+vu+" 视频资源入库失败"+" 异常信息："+e.getMessage());
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

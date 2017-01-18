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
public class MeipaiCrawlerData implements CrawlerData {
	public static Logger log = Logger.getLogger(MeipaiCrawlerData.class);
	public static Map<String, Object> crawlerFirstPage(String type, ResourceService resourceService) {
		Map<String, Object> result = new HashMap<String, Object>();
		String url = "http://www.meipai.com/square/" + type;
		Document doc = JsoupUtils.jsoupConnGet(url, null, null, 0);
		if (doc == null) {
			return null;
		}
		Elements els = doc.select("ul#mediasList>li");
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
			cv.setParentType("娱乐");
			try {
				resourceService.addCrawlerVideosingle(cv);
			} catch (Exception e1) {
				log.info(DateUtils.dateToString(new Date())+"  "+vu+" 视频资源入库失败"+" 异常信息："+e1.getMessage());
			}
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
			return null;
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
			cv.setParentType("娱乐");
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
		String str = HttpClient.get(url, null, 0);
		if (str == null) {
			
				return null;
			
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
			cv.setParentType("娱乐");
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

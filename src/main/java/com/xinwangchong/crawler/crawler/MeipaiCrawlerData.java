package com.xinwangchong.crawler.crawler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.alibaba.fastjson.JSON;
import com.xinwangchong.crawler.common.tools.ResourceUtils;
import com.xinwangchong.crawler.common.tools.Constant;
import com.xinwangchong.crawler.common.tools.HttpClient;
import com.xinwangchong.crawler.common.tools.JsoupUtils;
import com.xinwangchong.crawler.common.tools.StringUtils;
import com.xinwangchong.crawler.entity.CrawlerVideo;
import com.xinwangchong.crawler.service.ResourceService;

public class MeipaiCrawlerData implements CrawlerData {
	public static Map<String, Object> crawlerFirstPage(String type, ResourceService resourceService) {
		Map<String, Object> result = new HashMap<String, Object>();
		String url = "http://www.meipai.com/square/" + type;
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
		Elements els = doc.select("ul#mediasList>li");
		String id = null;
		CrawlerVideo cv = null;
		for (Element e : els) {
			cv = new CrawlerVideo();
			cv.setId(StringUtils.getUUID());
			cv.setImgUrl(e.select("img").attr("src"));
			Elements video_container = e.select("div.content-l-video.pr.cp");
			cv.setVideoUrl(video_container.attr("data-video"));
			cv.setTitle(StringUtils.getChineseInString(e.select("div.content-l-video.pr.cp>a.content-l-p.pa").text()));
			id = video_container.attr("data-id");
			cv.setType(ResourceUtils.getTypeName(type));
			cv.setSource(Constant.MEI_PAI);
			System.out.println(video_container.attr("data-video"));
			try {
				resourceService.addCrawlerVideosingle(cv);
			} catch (Exception e1) {
				e1.printStackTrace();
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
		String str = HttpClient.get(url, null);
		if (str == null) {
			for (int i = 0; i < 60; i++) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
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
			cv.setVideoUrl(map.get("video").toString());
			cv.setType(ResourceUtils.getTypeName(type));
			cv.setSource(Constant.MEI_PAI);
			System.out.println(map.get("video").toString());
			try {
				resourceService.addCrawlerVideosingle(cv);
			} catch (Exception e) {
				e.printStackTrace();
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
			cv.setVideoUrl(map.get("video").toString());
			cv.setType(ResourceUtils.getTypeName(type));
			id = map.get("id").toString();
			cv.setSource(Constant.MEI_PAI);
			System.out.println(map.get("video").toString());
			try {
				resourceService.addCrawlerVideosingle(cv);
			} catch (Exception e) {
				e.printStackTrace();
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
		/*String[] types_1 = { "13", "16", "19", "27", "31", "18" };
		String[] types_2 = { "59-5870490265939297486", "62-5871155236525660080", "63-5872239354896137479",
				"6-5864549574576746574", "423-6161763227134314911", "450-5875185672678760586" };*/
		String[] types_1 = { "13"};
		String[] types_2 = { "59-5870490265939297486"};
		String maxid = "";
		for (String type : types_1) {
			for (int i = 1; i <= pages; i++) {
				Map<String, Object> re = null;
				if (i > 1) {
					re = crawlerAjaxPage_1(i, type, resourceService);

				} else {
					re = crawlerFirstPage(type, resourceService);
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

package com.xinwangchong.crawler.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.alibaba.fastjson.JSON;
import com.xinwangchong.crawler.common.BizTools;
import com.xinwangchong.crawler.common.HttpClient;
import com.xinwangchong.crawler.common.StringUtils;
import com.xinwangchong.crawler.entity.CrawlerVideo;

public class Meipai {
	public static Map<String, Object> crawlerFirstPage(String type) {
		try {
			Map<String, Object> result=new HashMap<String, Object>();
			List<CrawlerVideo> cvs=new ArrayList<CrawlerVideo>();
			String url="http://www.meipai.com/square/"+type;
			Connection conn = Jsoup.connect(url);
			Document doc = conn.get();
			Elements els = doc.select("ul#mediasList>li");
			String id=null;
			CrawlerVideo cv=null;
			for (Element e : els) {
				cv=new CrawlerVideo();
				cv.setId(StringUtils.getUUID());
				cv.setImgUrl(e.select("img").attr("src"));
				Elements video_container = e.select("div.content-l-video.pr.cp");
				cv.setVideoUrl( video_container.attr("data-video"));
				cv.setTitle(StringUtils.getChineseInString(e.select("div.content-l-video.pr.cp>a.content-l-p.pa").text()));
				id=video_container.attr("data-id");
				cv.setType(BizTools.getTypeName(type));
				cvs.add(cv);
			}
			if (cvs.size()>0) {
				result.put("maxid", id);
				result.put("data", cvs);
				return result;
			}
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Map<String, Object> crawlerAjaxPage_1(int page,String type){
		String str = HttpClient.get("http://www.meipai.com/squares/new_timeline?page="+page+"&count=24&tid="+type,null);
		Map<String, Object> re = (Map<String, Object>) JSON.parse(str);
		List<Map<String, Object>> data = (List<Map<String, Object>>) JSON.parse(re.get("medias").toString());
		Map<String, Object> result=new HashMap<String, Object>();
		List<CrawlerVideo> cvs=new ArrayList<CrawlerVideo>();
		CrawlerVideo cv=null;
		for (Map<String, Object> map : data) {
			cv=new CrawlerVideo();
			cv.setId(StringUtils.getUUID());
			cv.setImgUrl(map.get("cover_pic").toString());
			cv.setTitle(StringUtils.getChineseInString(map.get("caption").toString()));
			cv.setVideoUrl(map.get("video").toString());
			cv.setType(BizTools.getTypeName(type));
			cvs.add(cv);
		}
		if (cvs.size()>0) {
			result.put("maxid", "");
			result.put("data", cvs);
			return result;
		}
		return null;
	}
	public static Map<String, Object> crawlerAjaxPage_2(int page,String type,String maxid){
		String url="http://www.meipai.com/topics/hot_timeline?page="+page+"&count=24&tid="+type+"&maxid="+maxid;
		String str = HttpClient.get(url,null);
		Map<String, Object> re = (Map<String, Object>) JSON.parse(str);
		List<Map<String, Object>> data = (List<Map<String, Object>>) JSON.parse(re.get("medias").toString());
		Map<String, Object> result=new HashMap<String, Object>();
		List<CrawlerVideo> cvs=new ArrayList<CrawlerVideo>();
		CrawlerVideo cv=null;
		String id=null;
		for (Map<String, Object> map : data) {
			cv=new CrawlerVideo();
			cv.setId(StringUtils.getUUID());
			cv.setImgUrl(map.get("cover_pic").toString());
			cv.setTitle(StringUtils.getChineseInString(map.get("caption").toString()));
			cv.setVideoUrl(map.get("video").toString());
			cv.setType(BizTools.getTypeName(type));
			id=map.get("id").toString();
			cvs.add(cv);
		}
		if (cvs.size()>0) {
			result.put("maxid", id);
			result.put("data", cvs);
			return result;
		}
		return null;
	}
	public static List<CrawlerVideo> crawler(){
		//http://www.meipai.com/topics/hot_timeline?page=4&count=24&tid=5870490265939297486&maxid=644457940
		//13.搞笑16.明星19.女神63.舞蹈27.美妆31.男神18.宝宝
		//59.美食(tid=5870490265939297486)
		//62.音乐(tid=5871155236525660080)
		//63.舞蹈(tid=5872239354896137479)
		//6.宠物(tid=5864549574576746574)
		//423.吃秀(tid=6161763227134314911)
		//450.手工(tid=5875185672678760586)
		String[] types_1={"13","16","19","27","31","18"};
		String[] types_2={"59-5870490265939297486",
				"62-5871155236525660080",
				"63-5872239354896137479",
				"6-5864549574576746574",
				"423-6161763227134314911",
				"450-5875185672678760586"};
		int pages=500;
		String maxid="";
		List<CrawlerVideo> cvs=new ArrayList<CrawlerVideo>();
		for (String type : types_1) {
			//System.out.println(type);
			for (int i = 1; i <= pages; i++) {
				Map<String, Object> re=null;
				if (i>1) {
					re = crawlerAjaxPage_1(i,type);
					
				}else{
					re = crawlerFirstPage(type);
				}
				List<CrawlerVideo> recvs = (List<CrawlerVideo>) re.get("data");
				cvs.addAll(recvs);
				
			}
		}
		for (String type : types_2) {
			//System.out.println(type);
			String[] split = type.split("-");
			for (int i = 1; i <= pages; i++) {
				Map<String, Object> re=null;
				if (i>1) {
					 re = crawlerAjaxPage_2(i,split[1],maxid);
				}else{
					re=crawlerFirstPage(split[0]);
				}
				List<CrawlerVideo> recvs = (List<CrawlerVideo>) re.get("data");
				maxid=(String) re.get("maxid");
				cvs.addAll(recvs);
				
			}
		}
		return cvs;
	}
	public static void main(String[] args) {
		List<CrawlerVideo> re = crawler();
		for (CrawlerVideo cv : re) {
			System.out.println("img:"+cv.getImgUrl());
			System.out.println("video:"+cv.getVideoUrl());
			System.out.println("title:"+cv.getTitle());
		}
	}
}

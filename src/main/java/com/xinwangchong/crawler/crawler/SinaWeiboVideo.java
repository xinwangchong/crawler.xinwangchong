package com.xinwangchong.crawler.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSON;
import com.xinwangchong.crawler.common.tools.BizTools;
import com.xinwangchong.crawler.common.tools.CrawlerVideoUtils;
import com.xinwangchong.crawler.common.tools.HttpClient;
import com.xinwangchong.crawler.common.tools.StringUtils;
import com.xinwangchong.crawler.entity.CrawlerVideo;
public class SinaWeiboVideo {
	public static Elements crawlerFirstPage(String type) {
		try {
			String url="http://weibo.com/tv/"+type;
			Connection conn = Jsoup.connect(url);
			Map<String, String> cookie=new HashMap<String, String>();
			cookie.put("SUB", "_2AkMvJRKKf8NhqwJRmP0cxWnna4VzzAHEieKZeeNRJRMxHRl-yT83qkNZtRBm8IFPLivtXz8hgb5iRvrP-4M1DQ..");
			conn.cookies(cookie);
			Document doc = conn.get();
			return  doc.select("div.weibo_tv_frame>ul>a");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static Elements crawlerAjaxPage(int page,String end_id,String type){
		String cookies = "SUB=_2AkMvJRKKf8NhqwJRmP0cxWnna4VzzAHEieKZeeNRJRMxHRl-yT83qkNZtRBm8IFPLivtXz8hgb5iRvrP-4M1DQ..";
		String str = HttpClient.get("http://weibo.com/p/aj/v6/mblog/videolist?type="+type+"&page="+page+"&end_id="+end_id+"&__rnd="+new Date().getTime(),cookies);
		Map<String, Object> re = (Map<String, Object>) JSON.parse(str);
		Map<String, Object> data = (Map<String, Object>) JSON.parse(re.get("data").toString());
		String elstr = data.get("data").toString();
		if (elstr!=null&&!"".equals(elstr)) {
			Document doc = Jsoup.parse(elstr);
			return doc.select("a");
		}
		return null;
	}
	public static Map<String, Object> parseHtml(Elements es,String basicUrl,String type){
		Map<String, Object> result=new HashMap<String, Object>();
		List<CrawlerVideo> cvs=new ArrayList<CrawlerVideo>();
		CrawlerVideo cv=null;
		String end_id=null;
		for (Element element : es) {
			cv=new CrawlerVideo();
			cv.setId(StringUtils.getUUID());
			String url = basicUrl+element.attr("href");
			Map<String, Object> reMap = CrawlerVideoUtils.getVideoByShuoshu(url,0);
			cv.setVideoUrl(reMap.get("videoUrl").toString());
			cv.setImgUrl(element.select("div.pic>img.piccut").attr("src"));
			cv.setType(BizTools.getTypeName(type));
			String title = element.select("div.intra_a>div.txt_cut").text();
			int http_index = title.lastIndexOf("http");
			if (http_index>-1) {
				title=title.trim().substring(0, title.lastIndexOf("http://"));	
			}else{
				title=title.trim();
			}
			cv.setTitle(title);
			end_id=element.attr("mid");
			cvs.add(cv);
		}
		if (cvs.size()>0) {
			result.put("maxid", end_id);
			result.put("data", cvs);
			return result;
		}
		return null;
	}
	public static List<CrawlerVideo> crawler(){
		String[] types={"vfun","movie","music","lifestyle","sports","world","moe","show"};
		int pages=1;
		String end_id="";
		List<CrawlerVideo> cvs=new ArrayList<CrawlerVideo>();
		String basicUrl="http://weibo.com";
		for (String type : types) {
			//System.out.println(type);
			for (int i = 1; i <= pages; i++) {
				Elements els=null;
				if (i>1) {
					els=crawlerAjaxPage(i,end_id,type);
				}else{
					els=crawlerFirstPage(type);
				}
				if (els==null) {
					//System.out.println(i);
					break;
				}
				Map<String, Object> re = parseHtml(els,basicUrl,type);
				if (re!=null) {
					List<CrawlerVideo> recvs = (List<CrawlerVideo>) re.get("data");
					end_id=(String) re.get("maxid");
					cvs.addAll(recvs);
				}
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

package com.xinwangchong.crawler.common.tools;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.xinwangchong.crawler.crawler.MeipaiCrawlerData;

public class JsoupUtils {
	public static Logger log = Logger.getLogger(MeipaiCrawlerData.class);
	public static Document jsoupConnGet(String url,Map<String, String> cookie, Map<String, String> params, int count){
		Document doc = null;
		try {
			Connection conn = Jsoup.connect(url);
			if (cookie!=null) {
				conn.cookies(cookie);
			}
			if (params!=null) {
				conn.data(params);
			}
			doc = conn.get();
			return doc;
		} catch (IOException e) {
			log.info("Jsoup 爬取："+url+"  失败");
			doc=null;
		} 
		if (doc==null&&count<=Constant.SEND_REQUEST_COUNT) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				
			}
			count++;
			return jsoupConnGet(url,cookie,params, count);
		}else{
			return null;
		}
	}
	public static Document jsoupConnPost(String url,Map<String, String> cookie,Map<String, String> params,int count){
		Document doc=null;
		try {
			Connection conn = Jsoup.connect(url);
			if (cookie!=null) {
				conn.cookies(cookie);
			}
			if (params!=null) {
				conn.data(params);
			}
			doc = conn.get();
			return doc;
		} catch (Exception e) {
			log.info("Jsoup 爬取："+url+"  失败");
			doc=null;
		}
		if (doc==null&&count<=Constant.SEND_REQUEST_COUNT) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				
			}
			count++;
			return jsoupConnPost(url,cookie,params, count);
		}else{
			return null;
		}
	}
	public static void main(String[] args) {
		Map<String, String> params=new HashMap<String, String>();
		params.put("pageNum", "1");
		params.put("saction", "search");
		Document re = jsoupConnPost("http://www.joy.cn/news/all", null, params,0);
		System.out.println(re);
	}
}

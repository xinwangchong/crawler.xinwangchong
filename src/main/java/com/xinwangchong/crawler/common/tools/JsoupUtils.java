package com.xinwangchong.crawler.common.tools;

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.xinwangchong.crawler.crawler.MeipaiCrawlerData;

public class JsoupUtils {
	public static Logger log = Logger.getLogger(MeipaiCrawlerData.class);
	public static Document jsoupConn(String url,Map<String, String> cookie){
		try {
			Thread.sleep(2000);
			Connection conn = Jsoup.connect(url);
			if (cookie!=null) {
				conn.cookies(cookie);
			}
			Document doc = conn.get();
			return doc;
		} catch (IOException e) {
			log.info("Jsoup 爬取："+url+"  失败");
		} catch (InterruptedException e) {
			log.info("Jsoup 爬取："+url+"  失败");
		}
		return null;
	}
}

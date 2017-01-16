package com.xinwangchong.crawler.common.tools;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class JsoupUtils {
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
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
}

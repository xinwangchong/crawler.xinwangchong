package com.xinwangchong.crawler.common.tools;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;
public class ShuoshuVideoUtils {
	public static Logger log = Logger.getLogger(ShuoshuVideoUtils.class);
	public static Map<String, Object> getVideoByShuoshu(String targetUrl,int count){
			Map<String, Object> map=null;
			String shuoshuUrl="http://www.flvcd.com/parse.php?format=&kw=";
			String url=shuoshuUrl+targetUrl;
			Document doc=JsoupUtils.jsoupConnGet(url,null, null, 0);
			if (doc==null) {
				for (int i = 0; i < 60; i++) {
					doc=JsoupUtils.jsoupConnGet(url,null, null, 0);
					if (doc!=null) {
						break;
					}
				}
				if (doc==null) {
					return null;
				}
			}
			Elements select = doc.select("a[onclick=_alert();return false;]");
			String videoUrl=select.get(0).attr("abs:href");
			String videoText=select.get(0).text();
			if (select!=null&&select.size()>0) {
				if((videoText!=null&&!videoText.trim().equals(""))||count>60){
					map=new HashMap<String, Object>();
					map.put("videoUrl", videoUrl);
					map.put("count", count);
					return map;
				}else{
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					map=new HashMap<String, Object>();
					map.put("videoUrl", videoUrl);
					map.put("count", count);
					count++;
					return getVideoByShuoshu(targetUrl,count);
				}
			}else{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					log.info(DateUtils.dateToString(new Date())+" Jsou 爬去硕鼠出现异常 "+e.getMessage());
					//e.printStackTrace();
				}
				count++;
				map=new HashMap<String, Object>();
				map.put("videoUrl", videoUrl);
				map.put("count", count);
				count++;
				return getVideoByShuoshu(targetUrl,count);
			}
	}
	public static void main(String[] args) {
		String targetUrl="http://weibo.com/tv/v/EqMn6i9aJ?from=vfun";
		getVideoByShuoshu(targetUrl,0);
	}
}

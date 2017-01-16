package com.xinwangchong.crawler.crawler;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.alibaba.fastjson.JSON;
import com.xinwangchong.crawler.common.tools.ResourceUtils;
import com.xinwangchong.crawler.common.tools.ShuoshuVideoUtils;
import com.xinwangchong.crawler.common.tools.Constant;
import com.xinwangchong.crawler.common.tools.HttpClient;
import com.xinwangchong.crawler.common.tools.JsoupUtils;
import com.xinwangchong.crawler.common.tools.StringUtils;
import com.xinwangchong.crawler.entity.CrawlerVideo;
import com.xinwangchong.crawler.service.ResourceService;
import com.xinwangchong.crawler.service.impl.ResourceServiceImpl;
public class SinaWeiboVideoCrawlerData {
	public static Elements crawlerFirstPage(String type) {
		String url="http://weibo.com/tv/"+type;
		Map<String, String> cookie=new HashMap<String, String>();
		cookie.put("SUB",Constant.SINA_COOKIE_SUB);
		Document doc = JsoupUtils.jsoupConn(url, cookie);
		if (doc==null) {
			for (int i = 0; i < 60; i++) {
				doc=JsoupUtils.jsoupConn(url, cookie);
				if (doc!=null) {
					break;
				}
			}
			if (doc==null) {
				return null;
			}
		}
		return  doc.select("div.weibo_tv_frame>ul>a");
	}
	public static Elements crawlerAjaxPage(int page,String end_id,String type){
		String url="http://weibo.com/p/aj/v6/mblog/videolist?type="+type+"&page="+page+"&end_id="+end_id+"&__rnd="+new Date().getTime();
		String str = HttpClient.get(url,"SUB="+Constant.SINA_COOKIE_SUB);
		if (str==null) {
			for (int i = 0; i < 60; i++) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				str = HttpClient.get(url,Constant.SINA_COOKIE_SUB);
				if (str!=null) {
					break;
				}
			}
			if (str==null) {
				return null;
			}
		}
		System.out.println(str);
		Map<String, Object> re = (Map<String, Object>) JSON.parse(str);
		Map<String, Object> data = (Map<String, Object>) JSON.parse(re.get("data").toString());
		String elstr = data.get("data").toString();
		if (elstr!=null&&!"".equals(elstr)) {
			Document doc = Jsoup.parse(elstr);
			return doc.select("a");
		}
		return null;
	}
	public static Map<String, Object> parseHtml(Elements es,String basicUrl,String type,ResourceService resourceService){
		Map<String, Object> result=new HashMap<String, Object>();
		List<CrawlerVideo> cvs=new ArrayList<CrawlerVideo>();
		CrawlerVideo cv=null;
		String end_id=null;
		for (Element element : es) {
			cv=new CrawlerVideo();
			cv.setId(StringUtils.getUUID());
			String url = basicUrl+element.attr("href");
			Map<String, Object> reMap = ShuoshuVideoUtils.getVideoByShuoshu(url,0);
			cv.setVideoUrl(reMap.get("videoUrl").toString());
			cv.setImgUrl(element.select("div.pic>img.piccut").attr("src"));
			cv.setType(ResourceUtils.getTypeName(type));
			String title = element.select("div.intra_a>div.txt_cut").text();
			int http_index = title.lastIndexOf("http");
			if (http_index>-1) {
				title=title.trim().substring(0, title.lastIndexOf("http://"));	
			}else{
				title=title.trim();
			}
			cv.setTitle(title);
			cv.setSource(Constant.SINA_WEIBO_VIDEO);
			end_id=element.attr("mid");
			try {
				resourceService.addCrawlerVideosingle(cv);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//System.out.println(reMap.get("videoUrl").toString());
			//cvs.add(cv);
		}
		if (end_id!=null&&!end_id.equals("")) {
			result.put("maxid", end_id);
			//result.put("data", cvs);
			return result;
		}
		return null;
	}
	public static List<CrawlerVideo> crawler(ResourceService resourceService,int pages){
		String[] types={"vfun","movie","music","lifestyle","sports","world","moe","show"};
		String end_id="";
		//List<CrawlerVideo> cvs=new ArrayList<CrawlerVideo>();
		String basicUrl="http://weibo.com";
		for (String type : types) {
			for (int i = 1; i <= pages; i++) {
				System.out.println("第"+i+"页");
				Elements els=null;
				if (i>1) {
					els=crawlerAjaxPage(i,end_id,type);
				}else{
					els=crawlerFirstPage(type);
				}
				if (els==null) {
					break;
				}
				Map<String, Object> re = parseHtml(els,basicUrl,type,resourceService);
				if (re!=null) {
					end_id=(String) re.get("maxid");
				}
			}
		}
			
		return null;
		
	}
	
}

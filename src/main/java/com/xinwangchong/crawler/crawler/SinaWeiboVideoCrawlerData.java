package com.xinwangchong.crawler.crawler;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.alibaba.fastjson.JSON;
import com.xinwangchong.crawler.common.tools.ResourceUtils;
import com.xinwangchong.crawler.common.tools.ShuoshuVideoUtils;
import com.xinwangchong.crawler.common.tools.Constant;
import com.xinwangchong.crawler.common.tools.DateUtils;
import com.xinwangchong.crawler.common.tools.HttpClient;
import com.xinwangchong.crawler.common.tools.JsoupUtils;
import com.xinwangchong.crawler.common.tools.StringUtils;
import com.xinwangchong.crawler.entity.CrawlerVideo;
import com.xinwangchong.crawler.service.ResourceService;
public class SinaWeiboVideoCrawlerData implements CrawlerData {
	public static Logger log = Logger.getLogger(SinaWeiboVideoCrawlerData.class);
	public static Elements crawlerFirstPage(String type) {
		String url="http://weibo.com/tv/"+type;
		Map<String, String> cookie=new HashMap<String, String>();
		cookie.put("SUB",Constant.SINA_COOKIE_SUB);
		Document doc = JsoupUtils.jsoupConnGet(url, cookie, null, 0);
		if (doc == null) {
			return null;
		}
		return  doc.select("div.weibo_tv_frame>ul>a");
	}
	public static Elements crawlerAjaxPage(int page,String end_id,String type){
		String url="http://weibo.com/p/aj/v6/mblog/videolist?type="+type+"&page="+page+"&end_id="+end_id+"&__rnd="+new Date().getTime();
		String str = HttpClient.get(url,"SUB="+Constant.SINA_COOKIE_SUB, 0);
		if (str==null) {
			
				return null;
			
		}
		Map<String, Object> re = null;
		try {
			re = (Map<String, Object>) JSON.parse(str);
		} catch (Exception e) {
			log.info(DateUtils.dateToString(new Date())+"  HttpClient -> get 爬取美拍异步分页数据失败 页码："+page+" 视频资源类型 ："+type+" 异常信息：返回了html原因是Sina Visitor System 权限校验失败");
			re=null;
		}
		if (re==null) {
			return null;
		}
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
		CrawlerVideo cv=null;
		String end_id=null;
		for (Element element : es) {
			cv=new CrawlerVideo();
			cv.setId(StringUtils.getUUID());
			String url = basicUrl+element.attr("href");
			Map<String, Object> reMap = ShuoshuVideoUtils.getVideoByShuoshu(url,0);
			String vu = reMap.get("videoUrl").toString();
			cv.setVideoUrl(vu);
			cv.setImgUrl(element.select("div.pic>img.piccut").attr("src"));
			cv.setType(ResourceUtils.getTypeName(type));
			String title = element.select("div.intra_a>div.txt_cut").text();
			int http_index = title.lastIndexOf("http");
			if (http_index>-1) {
				title=title.trim().substring(0, title.lastIndexOf("http://"));	
			}else{
				title=title.trim();
			}
			cv.setTitle(StringUtils.getChineseInString(title));
			cv.setSource(Constant.SINA_WEIBO_VIDEO);
			end_id=element.attr("mid");
			cv.setParentType("娱乐");
			if (vu.indexOf(".flv")>-1) {
				cv.setVideoType("flv");
			}
			else{
				cv.setVideoType("mp4");
			}
			try {
				resourceService.addCrawlerVideosingle(cv);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (end_id!=null&&!end_id.equals("")) {
			result.put("maxid", end_id);
			return result;
		}
		return null;
	}
	public Map<String, Object> crawler(ResourceService resourceService, int pages) {
		String[] types={"vfun","movie","music","lifestyle","sports","world","moe","show"};
		String end_id="";
		String basicUrl="http://weibo.com";
		for (String type : types) {
			for (int i = 1; i <= pages; i++) {
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

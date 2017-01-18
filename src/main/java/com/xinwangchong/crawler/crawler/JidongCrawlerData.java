package com.xinwangchong.crawler.crawler;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.xinwangchong.crawler.common.tools.Constant;
import com.xinwangchong.crawler.common.tools.DateUtils;
import com.xinwangchong.crawler.common.tools.JsoupUtils;
import com.xinwangchong.crawler.common.tools.StringUtils;
import com.xinwangchong.crawler.entity.CrawlerVideo;
import com.xinwangchong.crawler.service.ResourceService;
public class JidongCrawlerData implements CrawlerData {
	public static Logger log = Logger.getLogger(JidongCrawlerData.class);
	public static void main(String[] args) {
		new JidongCrawlerData().crawler(null, 1);
	}
	
	
	public Map<String, Object> crawler(ResourceService resourceService, int pages) {
		Map<String, String> types=new HashMap<String, String>();
		types.put("娱乐-明星", "http://www.joy.cn/entertainment/");
		types.put("新闻", "http://www.joy.cn/news/");
		types.put("体育", "http://www.joy.cn/sport/");
		CrawlerVideo cv = null;
		for (String type : types.keySet()) {
			for (int i = 1; i <= pages; i++) {
				
				Map<String, String> params=new HashMap<String, String>();
				params.put("pageNum", i+"");
				params.put("saction", "search");
				Document doc = JsoupUtils.jsoupConnPost(types.get(type)+"all", null, params, 0);
				Elements pager_a = doc.select("table.paginator a");
				if (i==1) {
					String a_text = pager_a.get(6).text();
					pages=Integer.parseInt(a_text.substring(1, a_text.length()-1));
				}
				Elements es = doc.select("div.content_right2>ul>li");
				for (Element e : es) {
					cv = new CrawlerVideo();
					cv.setId(StringUtils.getUUID());
					if (type.indexOf("-")>-1) {
						cv.setParentType(type.split("-")[0]);
						cv.setType(type.split("-")[1]);
					}else{
						cv.setParentType(type);
						cv.setType(type);
					}
					cv.setSource(Constant.JI_DONG_VIDEO);
					cv.setTitle(StringUtils.getChineseInString(e.select("a.joy_item_a").text()));
					cv.setImgUrl(e.select("img").attr("src"));
					String videoHtmlUrl = e.select("a.joy_item_a").attr("href");
					Document vd = JsoupUtils.jsoupConnPost(types.get(type)+videoHtmlUrl, null, params, 0);
					cv.setVideoUrl(vd.select("source").attr("src"));
					System.out.println(cv.getImgUrl());
					System.out.println(cv.getTitle());
					System.out.println(cv.getVideoUrl());
					try {
						resourceService.addCrawlerVideosingle(cv);
					} catch (Exception e1) {
						log.info(DateUtils.dateToString(new Date())+"  "+types.get(type)+videoHtmlUrl+" 视频资源入库失败"+" 异常信息："+e1.getMessage());
					}
				}
				
			}
		}
		return null;
	}
}

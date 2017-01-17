package com.xinwangchong.crawler.quartz;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.xinwangchong.crawler.common.tools.Constant;
import com.xinwangchong.crawler.common.tools.DateUtils;
import com.xinwangchong.crawler.crawler.CrawlerData;
import com.xinwangchong.crawler.crawler.MeipaiCrawlerData;
import com.xinwangchong.crawler.crawler.SinaWeiboVideoCrawlerData;
import com.xinwangchong.crawler.service.ResourceService;

public class UpdateResourceJob {
	public static Logger log = Logger.getLogger(UpdateResourceJob.class);
	@Resource
	private ResourceService resourceService;
	public static Date deleteTime=null;
	public void worker() {
		try {
			crawlerMeipaiWork(resourceService,1,new MeipaiCrawlerData());
		} catch (Exception e) {
			log.info(DateUtils.dateToString(new Date())+" 抓取美拍视频此次调度失败");
		}
		try {
			crawlerSinaWeiboWork(resourceService,1,new SinaWeiboVideoCrawlerData());
		} catch (Exception e) {
			log.info(DateUtils.dateToString(new Date())+" 抓取新浪微博视频此次调度失败");
		}
	}
	private void crawlerMeipaiWork(ResourceService rs,int pages,CrawlerData crawlerData) throws Exception{
		crawlerData.crawler(rs,pages);
		if (deleteTime!=null) {
			rs.removeResource(deleteTime, Constant.MEI_PAI);
		}
		deleteTime=new Date();
	}
	private void crawlerSinaWeiboWork(ResourceService rs,int pages,CrawlerData crawlerData) throws Exception{
		crawlerData.crawler(rs,pages);
		if (deleteTime!=null) {
			rs.removeResource(deleteTime, Constant.SINA_WEIBO_VIDEO);
		}
		deleteTime=new Date();
	}
}

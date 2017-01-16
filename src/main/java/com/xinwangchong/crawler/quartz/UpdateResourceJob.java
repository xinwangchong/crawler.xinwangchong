package com.xinwangchong.crawler.quartz;

import java.util.Date;

import javax.annotation.Resource;

import com.xinwangchong.crawler.common.tools.Constant;
import com.xinwangchong.crawler.crawler.CrawlerData;
import com.xinwangchong.crawler.crawler.MeipaiCrawlerData;
import com.xinwangchong.crawler.service.ResourceService;

public class UpdateResourceJob {
	@Resource
	private ResourceService resourceService;
	public static Date deleteTime=null;
	public void worker() {
		try {
			crawlerMeipaiWork(resourceService,2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*try {
			crawlerSinaWeiboWork(resourceService);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
	public void crawlerMeipaiWork(ResourceService rs,int pages) throws Exception{
		CrawlerData crawlerData=new MeipaiCrawlerData();
		crawlerData.crawler(rs,pages);
		System.out.println("插入完毕，开始删除");
		if (deleteTime!=null) {
			rs.removeResource(deleteTime, Constant.MEI_PAI);
			System.out.println("删除完毕");
		}
		deleteTime=new Date();
	}
	/*public void crawlerSinaWeiboWork(ResourceService rs,int pages) throws Exception{
		SinaWeiboVideo.crawler(rs,500);
		if (deleteTime!=null) {
			deleteTime=new Date();
			rs.removeResource(deleteTime, Constant.SINA_WEIBO_VIDEO);
		}
	}*/
}

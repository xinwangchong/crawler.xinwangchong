package com.xinwangchong.crawler.quartz;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import com.xinwangchong.crawler.common.tools.DateUtils;
import com.xinwangchong.crawler.common.tools.FileUtils;
import com.xinwangchong.crawler.common.tools.StringUtils;
import com.xinwangchong.crawler.crawler.Meipai;
import com.xinwangchong.crawler.entity.CrawlerVideo;
import com.xinwangchong.crawler.service.ResourceService;

public class UpdateResourceJob {
	@Resource
	private ResourceService resourceService;
	public void worker() {
		try {
			Meipai.crawler(resourceService);
			/*List<CrawlerVideo> cvs=new ArrayList<CrawlerVideo>();
			CrawlerVideo cv=new CrawlerVideo();
			cv.setId(StringUtils.getUUID());
			cvs.add(cv);
			resourceService.addResource(cvs);*/
			/*String content=DateUtils.dateToString(new Date())+" 爬取"+cvs.size()+"条视频入库\r\n";
			FileUtils.contentToTxt("G:/crawler.log", content);*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

package com.xinwangchong.crawler.service.impl;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import com.xinwangchong.crawler.dao.CrawlerVideoDao;
import com.xinwangchong.crawler.entity.CrawlerVideo;
import com.xinwangchong.crawler.service.ResourceService;
@Service
public class ResourceServiceImpl implements ResourceService {
	
	@Resource
	private CrawlerVideoDao crawlerVideoDao;
	
	public int addResource(List<CrawlerVideo> cv) throws Exception {
		return crawlerVideoDao.insertCrawlerVideo(cv);
	}

	public int removeResource(Date dateTime,String source) throws Exception {
		return crawlerVideoDao.deletCrawlerVideo(dateTime,source);
	}

	public int addCrawlerVideosingle(CrawlerVideo cv) throws Exception {
		Thread.sleep(2000);
		return crawlerVideoDao.insertCrawlerVideosingle(cv);
	}

}

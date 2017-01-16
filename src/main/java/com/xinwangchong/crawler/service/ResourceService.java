package com.xinwangchong.crawler.service;

import java.util.Date;
import java.util.List;

import com.xinwangchong.crawler.entity.CrawlerVideo;

public interface ResourceService {
	
	public int addResource(List<CrawlerVideo> cv) throws Exception;
	
	public int addCrawlerVideosingle(CrawlerVideo cv)throws Exception;
	
	public int removeResource(Date dateTime) throws Exception;
}

package com.xinwangchong.crawler.crawler;

import java.util.Map;

import com.xinwangchong.crawler.service.ResourceService;

public interface CrawlerData {
	public Map<String, Object> crawler(ResourceService resourceService, int pages);
}

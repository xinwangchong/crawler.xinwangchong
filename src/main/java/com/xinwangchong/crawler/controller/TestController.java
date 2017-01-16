package com.xinwangchong.crawler.controller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xinwangchong.crawler.common.tools.StringUtils;
import com.xinwangchong.crawler.dao.CrawlerVideoDao;
import com.xinwangchong.crawler.entity.CrawlerVideo;
import com.xinwangchong.crawler.service.ResourceService;

@Controller
@RequestMapping("/demo")
public class TestController {
	@Resource
	private ResourceService resourceService;
	@RequestMapping(value = "test", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> mobilehome001_single() throws Exception {
		Map<String, Object> map=new HashMap<String, Object>();
		List<CrawlerVideo> cvs=new ArrayList<CrawlerVideo>();
		CrawlerVideo cv=new CrawlerVideo();
		cv.setId(StringUtils.getUUID());
		cvs.add(cv);
		resourceService.addResource(cvs);
		map.put("data", "张盼枝");
		return map;
	}
	 
}

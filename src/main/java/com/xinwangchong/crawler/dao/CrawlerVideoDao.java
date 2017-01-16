package com.xinwangchong.crawler.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.xinwangchong.crawler.entity.CrawlerVideo;
@Repository
public interface CrawlerVideoDao {
	
	public int insertCrawlerVideo(List<CrawlerVideo> cvs) throws Exception;
	
	public int insertCrawlerVideosingle(CrawlerVideo cv)throws Exception;

	public int deletCrawlerVideo(@Param("createTime")Date dateTime,@Param("source")String source) throws Exception;
}
  
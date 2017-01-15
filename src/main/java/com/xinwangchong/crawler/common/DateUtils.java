package com.xinwangchong.crawler.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
public  static String dateToString(Date date){
	SimpleDateFormat sf=new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
	return sf.format(date);
}
}

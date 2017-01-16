package com.xinwangchong.crawler.common.tools;

import java.util.UUID;

public class StringUtils {
	public static String getChineseInString(String str) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			if ((str.charAt(i) + "").getBytes().length > 1) {
				sb.append(str.charAt(i));
			}
		}
		return sb.toString();
	}
	public static String getUUID(){
		return UUID.randomUUID().toString().replace("-", "");
	}
	public static void main(String[] args) {
		System.out.println(getUUID());
	}
}

package com.xinwangchong.crawler.common.tools;

import java.util.HashMap;
import java.util.Map;

public class ResourceUtils {
	public static String getTypeName(String tid) {
		Map<String, String> types = allType();
		return types.get(tid);
	}

	public static Map<String, String> allType() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("13", "搞笑");
		map.put("16", "明星");
		map.put("19", "女神");
		map.put("27", "美妆");
		map.put("31", "男神");
		map.put("18", "萌娃");
		map.put("59", "美食");
		map.put("62", "音乐");
		map.put("63", "舞蹈");
		map.put("6", "宠物");
		map.put("423", "吃秀");
		map.put("450", "手工");
		map.put("5870490265939297486", "美食");
		map.put("5871155236525660080", "音乐");
		map.put("5872239354896137479", "舞蹈");
		map.put("5864549574576746574", "宠物");
		map.put("6161763227134314911", "吃秀");
		map.put("5875185672678760586", "手工");
		map.put("vfun", "搞笑");
		map.put("movie", "影视");
		map.put("music", "音乐");
		map.put("lifestyle", "生活");
		map.put("sports", "健身");
		map.put("world", "世界");
		map.put("moe", "萌娃");
		map.put("show", "明星");
		return map;
	}
}

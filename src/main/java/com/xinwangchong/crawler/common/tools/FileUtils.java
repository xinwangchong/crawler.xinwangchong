package com.xinwangchong.crawler.common.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class FileUtils {
	public static void contentToTxt(String filePath, String content) {
		String str = new String(); // 原有txt内容
		String s1 = new String();// 内容更新
		try {
			File f = new File(filePath);
			if (!f.exists()) {
				f.createNewFile();
			}
			BufferedReader input = new BufferedReader(new FileReader(f));
			while ((str = input.readLine()) != null) {
				s1 += str + "\n";
			}
			input.close();
			s1 += content;

			BufferedWriter output = new BufferedWriter(new FileWriter(f));
			output.write(s1);
			output.close();
		} catch (Exception e) {
			e.printStackTrace();

		}
	}
}

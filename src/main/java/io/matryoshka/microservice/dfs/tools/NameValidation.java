package io.matryoshka.microservice.dfs.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.RandomStringUtils;

public class NameValidation {

	/**
     * 判断字符串中是否包含中文
     * @param str
     * 待校验字符串
     * @return 是否为中文
     * @warn 不能校验是否为中文标点符号
     */
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }
    
    /**
     * 对于目录名为中文,生成随机字符串映射到minio中
     * @return
     */
    public static String mapperDirName(){
    	String mapperDirName = RandomStringUtils.randomAlphanumeric(10).toLowerCase();
		return mapperDirName;
    } 
}

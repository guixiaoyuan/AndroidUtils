package com.example.guixiaoyuan.utils;

import java.util.Random;

/**
 * @author guixiaoyuan
 * @version 2020-05-09
 */
public class StringUtils {


    /**
     * 随机生成多位数字字母字符串
     *
     * @param length 字符串长度
     * @return
     */
    public static String getRandomString(int length) {
        String results = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            if ("char".equals(charOrNum)) {
                int intVal = (int) (Math.random() * 26 + 97);
                char str = (char) intVal;

                results = results + str;
            } else {
                results += String.valueOf(random.nextInt(10));
            }
        }
        return results;
    }
}

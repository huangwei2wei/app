package com.app.server.util;

public class MatchUtils {
    /**
     * 获取start到end的随机数(不包括end，不包括负数)
     * @param start
     * @param end 
     * @return
     */
    public static int getRandomNum(int start, int end){
        int ret = (int)(Math.random()*(end-start)+start);
        return ret;
    }
}

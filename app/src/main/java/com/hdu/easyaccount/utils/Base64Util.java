package com.hdu.easyaccount.utils;

import android.util.Base64;

import java.util.Arrays;

/**
 * 操作Base64的封装类
 */

public class Base64Util {
    /**
     * 将字符串进行Base64转码并返回转码后字符串
     * @param str 转换的字符串
     * @return 转码后的字符串
     */
    public static String encode(String str) {
        return Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
    }

    public static String decode(String str) {
        return new String(Base64.decode(str, Base64.DEFAULT));
    }
}

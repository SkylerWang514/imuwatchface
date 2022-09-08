package com.zheolls.zenfacedigit.utils;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringEncoder {
    /**
     * 使用 md5 摘要算法进行加密
     *
     * @param string 原始字符串
     * @return 加密得到的 md5 串
     *         - 字符串为空时，将返回一个空串
     *         - 加密失败也将返回一个空串
     */
    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            return bytesToString(messageDigest.digest(string.getBytes()));
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 使用 md5 摘要算法进行加密
     * 使用字符串的 长度 作为盐
     *
     * @param string 原始字符串
     * @return 加密得到的 md5 串
     *         - 字符串为空时，将返回一个空串
     *         - 加密失败也将返回一个空串
     */
    public static String md5WithSalt(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            return bytesToString(messageDigest.digest((string + string.length()).getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 将通过 md5 加密后的字节数组转换为字符串
     * @param bytesString 加密产生的字节数组
     * @return 对应的字符串
     */
    private static String bytesToString(byte[] bytesString) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytesString) {
            if ((b & 0xFF) < 0x10) {
                stringBuilder.append(0);
            }
            stringBuilder.append(Integer.toHexString(b & 0xFF));
        }
        return stringBuilder.toString();
    }
}

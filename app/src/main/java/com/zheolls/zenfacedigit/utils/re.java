package com.zheolls.zenfacedigit.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class re {
    public static String getpairstring(String re, String string) {
        Pattern pattern = Pattern.compile(re);
        Matcher matcher = pattern.matcher(string);
        while (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public static String getpairstring(String re, String string, int index) {
        Pattern pattern = Pattern.compile(re);
        Matcher matcher = pattern.matcher(string);
        boolean flag = false;
        while (index > 0) {
            flag = matcher.find();
        }
        if (flag) {
            return matcher.group();
        } else
            return null;
    }
}

package com.zheolls.zenfacedigit.utils;

import android.os.SystemClock;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {
    /**
     * 将 传感器时间戳 转换为 Unix时间戳
     * 关于传感器时间戳的详细介绍请查看如下链接:
     *     https://developer.android.google.cn/reference/android/hardware/SensorEvent?hl=en#timestamp
     * - A: SystemClock.elapsedRealtimeNanos() 返回自开机以来的 纳秒数
     * - B: 传感器时间戳是 纳秒级 的,基于 A 增长
     * - 因此 Unix time =
     */
    public static long sensorEventTimestampToUnixTimestamp(long timestamp) {
        return (new Date()).getTime() + (timestamp - SystemClock.elapsedRealtimeNanos()) / 1000000L;
    }

    // 毫秒级日期时间格式化
    private static final SimpleDateFormat milliSecondLevelDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss_SSS", Locale.CHINESE);
    // 毫秒级时间格式化
    private static final SimpleDateFormat timeOnlyDateFormat = new SimpleDateFormat("HH:mm:ss.SSS", Locale.CHINESE);
    // 秒级日期时间格式化
    private static final SimpleDateFormat secondDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.CHINESE);
    // 日期级格式化
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE);

    public static String milliSecondDatetime(long timeStamp) {
        return milliSecondLevelDateFormat.format(timeStamp);
    }

    public static String timeOnlyDatetime(long timeStamp) {
        return timeOnlyDateFormat.format(timeStamp);
    }

    public static String secondDatetime(long timeStamp) {
        return secondDateFormat.format(timeStamp);
    }

    public static String dateDatetime(long timestamp) {
        return dateFormat.format(timestamp);
    }

    // 将日期时间转换为在文件名中可用的格式
    public static String filenameUsableTimeString(String timeString) {
        return timeString.replace(':', '-');
    }
}

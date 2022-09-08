package com.zheolls.zenfacedigit.network.applications;


/**
 * 数据收集 应用相关的常量数据
 *  - 应用名称
 *  - 可用 url
 */
public class DataCollect {
    public static final String name = "data_collect";
    // 所有可用的 url
    public static final class urls {
        // 上传一个文件
        public static final String uploadOneFile = "uploadOneFile/";
        // 一次性上传多个文件
        public static final String uploadFiles = "uploadFiles/";
    }
    // 所有可用的文件辨识符
    public static final class fileTags {
        // 传感器数据
        public static final String sensorData = "sensor_data";
        // 时间戳记录
        public static final String timestampRecord = "timestamp_record";
    }
}

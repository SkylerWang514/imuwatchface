package com.zheolls.zenfacedigit.network;

import com.zheolls.zenfacedigit.network.applications.Database;

public class UrlBuilder {
    public static String builder(String application, String request) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ServerInfo.getBaseUrl())
                .append(application).append("/")
                .append(request);
        return stringBuilder.toString();
    }

    /**
     * 用于构造通过 url 访问文件的请求链接
     *
     * @param filePathFromDatabase 文件在数据库中的相对路径
     * @return 构造完成的请求 url
     */
    public static String fileUrl(String filePathFromDatabase) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ServerInfo.getBaseUrl())
                .append(Database.name).append("/")
                .append(Database.urls.files)
                .append(filePathFromDatabase);
        return stringBuilder.toString();
    }
}

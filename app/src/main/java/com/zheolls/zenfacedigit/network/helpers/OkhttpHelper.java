package com.zheolls.zenfacedigit.network.helpers;

import android.util.Log;

import com.zheolls.zenfacedigit.network.NetworkClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.OkHttpClient;

/**
 * 从服务器下载文件时帮助类
 */
public class OkhttpHelper {
    protected String TAG = null;
    // GET 请求的参数
    protected HashMap<String, String> getParams = null;
    // POST 请求参数
    private JsonObject postParams = null;
    // 上传文件时，暂存文件
    protected HashMap<String, ArrayList<File>> fileLists = null;
    // 构造请求的 url
    protected String url = null;
    // http 客户端指针
    protected OkHttpClient client = null;
    // 响应内容
    protected String responseContent = null;
    // Gson类，用于解析响应内容
    protected static final Gson gson = new Gson();

    // 加载动画提示信息
    private String playerMessage;

    /**
     * 构造一个请求帮助类
     *
     * @param url    请求的url
     * @param client OkHttpClient，用于发起请求
     */
    public OkhttpHelper(final String url, final OkHttpClient client) {
        TAG = getClass().getSimpleName();
        postParams = new JsonObject();
        this.url = url;
        this.client = client;
    }

    /**
     * 添加一个 Number类型的 Post 请求参数
     */
    public void addPostParam(String key, Number value) {
        postParams.addProperty(key, value);
    }

    /**
     * 添加一个 Boolean类型的 Post 请求参数
     */
    public void addPostParam(String key, Boolean value) {
        postParams.addProperty(key, value);
    }

    /**
     * 添加一个 String类型的 Post 请求参数
     */
    public void addPostParam(String key, String value) {
        postParams.addProperty(key, value);
    }

    /**
     * 构造 POST 参数的 JSON 格式字符串
     */
    protected String getPostParamsString() {
        return postParams.toString();
    }

    /**
     * 获取已添加的文件数量
     */
    protected int getFileCount() {
        int count = 0;
        for (ArrayList<File> fileList : fileLists.values()) {
            count += fileList.size();
        }
        return count;
    }

    /**
     * 获取内部保存的所有文件的列表
     */
    protected ArrayList<File> getAllFiles() {
        ArrayList<File> files = new ArrayList<>();
        for (ArrayList<File> fileList : fileLists.values()) {
            files.addAll(fileList);
        }
        return files;
    }

    /**
     * 删除内部的所有文件，返回删除成功的个数
     * 同时将清除 fileLists 中所有的数据
     */
    protected int deleteAllFiles() {
        int count = 0;
        for (File file : getAllFiles()) {
            count += file.delete() ? 1 : 0;
        }
        // 清除 fileLists 中保存的文件信息
        fileLists.clear();
        return count;
    }

    /**
     * 在调试模式下，将响应内容以日志的形式输出
     */
    protected void jumpToDebugActivity() {
        if (NetworkClient.debugMode) {
            Log.e(TAG, responseContent);
        } else {
            Log.e(TAG, "不处于调试模式，不输出调试内容");
        }
    }
}

package com.zheolls.zenfacedigit.network;

import android.util.Log;

import com.zheolls.zenfacedigit.network.helpers.download.DownloadHelper;
import com.zheolls.zenfacedigit.network.helpers.request.RequestHelper;
import com.zheolls.zenfacedigit.network.helpers.upload.UploadHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class NetworkClient {
    // 单例类的实例
    private static NetworkClient networkClient;
    // 默认超时毫秒数
    private static long defaultTimeoutMillis = -1;
    // 不同超时值对应的 OkHttpClient 实例
    private static final Map<Long, OkHttpClient> clients = new HashMap<>();
    // 是否开启调试模式，控制在发生不能处理的错误时是否跳转到 DebugActivity 活动
    public static final boolean debugMode = true;

    private NetworkClient() {
        // 通过单例类的包裹，保证不同超时时间的 OkHttpClient 实例只有一个
        final OkHttpClient defaultClient = new OkHttpClient();
        clients.put((long) defaultClient.readTimeoutMillis(), defaultClient);
        defaultTimeoutMillis = defaultClient.readTimeoutMillis();
    }

    private static NetworkClient getInstance() {
        if (networkClient == null) {
            synchronized (NetworkClient.class) {
                if (networkClient == null) {
                    networkClient = new NetworkClient();
                }
            }
        }
        return networkClient;
    }

    private static void checkClient(final long timeoutMillis) {
        if (getInstance().clients.containsKey(timeoutMillis) == false) {
            clients.put(
                    timeoutMillis,
                    new OkHttpClient().newBuilder()
                            .connectTimeout(timeoutMillis, TimeUnit.MILLISECONDS)
                            .readTimeout(timeoutMillis, TimeUnit.MILLISECONDS)
                            .writeTimeout(timeoutMillis, TimeUnit.MILLISECONDS)
                            .build()
            );
        }
        Log.i(NetworkClient.class.getSimpleName(), "客户端数量：" + clients.size());
    }

    public static RequestHelper request(final String url) {
        return new RequestHelper(url, getInstance().clients.get(defaultTimeoutMillis));
    }

    public static RequestHelper request(final String url, final long timeoutMillis) {
        checkClient(timeoutMillis);
        return new RequestHelper(url, getInstance().clients.get(timeoutMillis));
    }

    public static UploadHelper upload(final String url) {
        return new UploadHelper(url, getInstance().clients.get(defaultTimeoutMillis));
    }

    public static UploadHelper upload(final String url, final long timeoutMillis) {
        checkClient(timeoutMillis);
        return new UploadHelper(url, getInstance().clients.get(timeoutMillis));
    }

    public static DownloadHelper download(final String url) {
        return new DownloadHelper(url, getInstance().clients.get(defaultTimeoutMillis));
    }

    public static DownloadHelper download(final String url, final long timeoutMillis) {
        checkClient(timeoutMillis);
        return new DownloadHelper(url, getInstance().clients.get(timeoutMillis));
    }

    public static long getDefaultTimeoutMillis() {
        return getInstance().defaultTimeoutMillis;
    }
}

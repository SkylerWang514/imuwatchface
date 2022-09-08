package com.zheolls.zenfacedigit.network;

import android.util.Log;

import com.zheolls.zenfacedigit.utils.LocalCache;

public class ServerInfo {
    private static final String TAG = ServerInfo.class.getSimpleName();

    // 服务器IP地址
    private static String IP = "124.221.65.120";
    // 服务器端口
    private static final String PORT = "8001";
    // 请求 URL 的前缀
    private static String BASE_URL = "http://" + IP + ":" + PORT + "/";

    // 本地缓存中保存的服务器IP地址
    private static final String serverAddressKey = "serverAddress";

    public static String getIP() {
        return IP;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    /**
     * 尝试使用本地缓存初始化服务器IP地址，否则使用预设的IP地址
     */
    public static void init() {
        Log.i(TAG, "初始化服务器IP地址");
        IP = (String) LocalCache.getData(serverAddressKey, IP);
        BASE_URL = "http://" + IP + ":" + PORT + "/";
    }

    /**
     * @param serverAddress 新的IP地址
     */
    public static boolean updateServerInfo(final String serverAddress) {
        if (isValidIPAddress(serverAddress)) {
            IP = serverAddress;
            BASE_URL = "http://" + IP + ":" + PORT + "/";
            // 写入本地缓存中
            LocalCache.putData(serverAddressKey, IP);
            Log.i(TAG, "更新服务器IP地址成功：" + IP);
            return true;
        }
        Log.i(TAG, "IP地址不合法，放弃更新");
        return false;
    }

    /**
     * 校验IP地址是否是合法的IPV4地址
     *
     * @param ip ip地址
     * @return true: 合法，false: 不合法
     */
    public static boolean isValidIPAddress(final String ip) {
        // 首先进行基本的检查
        if (ip.length() < 7 || ip.length() > 15) {
            return false;
        }
        String[] spiltIp = ip.split("\\.");
        if (spiltIp.length == 4) {
            int temp = -1;
            for (String ipInfo : spiltIp) {
                temp = Integer.parseInt(ipInfo);
                if (!(temp > -1 && temp < 256)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}

package com.zheolls.zenfacedigit.network.helpers.request;

/**
 * 定义在从服务器获取响应数据之后如何处理这些数据
 */
public interface DataBack<T> {
    /**
     * 获得服务器响应之后处理响应，需要注意的是这里只是服务器成功响应请求
     * 并不意味着成功拿回了想要的数据
     */
    void onResponse(T data);

    /**
     * 请求失败时执行什么动作，这意味着没能接收到服务器的响应
     */
    void onFailure(String message);
}

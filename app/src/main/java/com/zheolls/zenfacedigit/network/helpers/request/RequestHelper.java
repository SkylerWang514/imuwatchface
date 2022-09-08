package com.zheolls.zenfacedigit.network.helpers.request;

import android.util.Log;

import com.zheolls.zenfacedigit.network.beans.CommonBean;
import com.zheolls.zenfacedigit.network.beans.CommonListBean;
import com.zheolls.zenfacedigit.network.helpers.OkhttpHelper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 向服务器发起请求帮助类
 */
public class RequestHelper extends OkhttpHelper {
    // 用于 Gson 解析数据时构造对象
    protected Class dataBackClass = null;
    // Gson类，用于解析响应内容
    private static final Gson gson = new Gson();

    /**
     * 构造一个请求帮助类
     *
     * @param url    请求的url
     * @param client OkHttpClient，用于发起请求
     */
    public RequestHelper(String url, OkHttpClient client) {
        super(url, client);
        getParams = new HashMap<>();
    }

    /**
     * 设置 Gson 解析响应内容构造对象时使用的类
     * 传入 Object.class 类字面量，类似Activity跳转时的第二个参数
     */
    public <T> void setDataBackClass(Class<T> dataBackClass) {
        this.dataBackClass = dataBackClass;
    }

    /**
     * 添加一个 Get 请求参数
     *
     * @param key   String 键
     * @param value String 只接受字符串类型
     */
    public void addGetParam(String key, String value) {
        getParams.put(key, value);
    }

    /**
     * 构造 GET 参数的字符串
     */
    private String getGetParamsString() {
        StringBuilder paramsBuilder = new StringBuilder();
        paramsBuilder.append("?");
        for (Map.Entry<String, String> entry : getParams.entrySet()) {
            paramsBuilder
                    .append(entry.getKey()).append("=")
                    .append(entry.getValue()).append("&");
        }
        return paramsBuilder.toString();
    }

    /**
     * 向服务器发送 GET 请求，将会把使用 addGetParam 添加的参数添加到请求 url 中
     *
     * @param dataBack 定义在收到响应时如何处理数据的接口
     */
    public <T> void doGet(final DataBack<T> dataBack) {
        url += getGetParamsString();
        Log.e(TAG, "doGet: " + url);

        // 构造请求
        final Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        final Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                final String message = e.getClass().getSimpleName().equals("ConnectException") ?
                        "连接服务器失败" : "未知连接错误";
                Log.e(TAG, "Get请求失败：" + e.getMessage());
                // 输出异常调用栈
                e.printStackTrace();

                dataBack.onFailure(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) {
                // 响应状态码
                final int responseCode = response.code();
                // 获取响应内容
                try {
                    responseContent = Objects.requireNonNull(response.body()).string();
                } catch (IOException e) {
                    responseContent = "";
                    e.printStackTrace();
                }
                // 首先检查请求是否失败，若失败则直接返回
                if (!response.isSuccessful()) {
                    dataBack.onFailure("请求失败，状态码：" + responseCode);
                    jumpToDebugActivity();
                    return;
                }

                Log.i(TAG, responseContent);

                try {
                    // 构造响应结果
                    T responseData = (T) gson.fromJson(responseContent, dataBackClass);
                    dataBack.onResponse(responseData);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    dataBack.onFailure("解析数据失败");
                    CommonBean commonData = gson.fromJson(responseContent, CommonBean.class);
                    Log.e(TAG, "解析数据失败：\n" +
                            "状态码：" + commonData.getStatusCode() + "\n" +
                            "状态码描述信息：" + commonData.getStatusCode().getDescription() + "\n" +
                            "服务器提示信息：" + commonData.getTip()
                    );
                }
            }
        });
    }

    /**
     * 向服务器发送 POST 请求，将会把使用 addPostParam 添加的参数添加到请求中
     *
     * @param dataBack 定义在收到响应时如何处理数据的接口
     */
    public <T> void doPostJson(final DataBack<T> dataBack) {
        Log.e(TAG, "doPostJson: " + url);
        // 构造请求
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json;charset=utf-8"),
                getPostParamsString());
        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        final Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                final String message = e.getClass().getSimpleName().equals("ConnectException") ?
                        "连接服务器失败" : "未知连接错误";
                Log.e(TAG, "POST请求失败：" + e.getMessage() + ", url: " + url);


                // 输出异常调用栈
                e.printStackTrace();
                dataBack.onFailure(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) {
                // 响应状态码
                final int responseCode = response.code();
                // 获取响应内容
                try {
                    responseContent = Objects.requireNonNull(response.body()).string();
                } catch (IOException e) {
                    responseContent = "";
                    e.printStackTrace();
                }
                // 首先检查请求是否失败，若失败则直接返回
                if (!response.isSuccessful()) {
                    dataBack.onFailure("请求失败，状态码：" + responseCode);
                    jumpToDebugActivity();
                    return;
                }

                Log.i(TAG, responseContent);

                try {
                    // 构造响应结果
                    T responseData = (T) gson.fromJson(responseContent, dataBackClass);
                    dataBack.onResponse(responseData);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    dataBack.onFailure("解析数据失败");
                    try {
                        CommonBean commonData = gson.fromJson(responseContent, CommonBean.class);
                        Log.e(TAG, "解析数据失败：\n" +
                                "状态码：" + commonData.getStatusCode() + "\n" +
                                "状态码描述信息：" + commonData.getStatusCode().getDescription() + "\n" +
                                "服务器提示信息：" + commonData.getTip()
                        );
                    }
                    catch (JsonSyntaxException jsonSyntaxException) {
                        CommonListBean commonData = gson.fromJson(responseContent, CommonListBean.class);
                        Log.e(TAG, "解析数据失败：\n" +
                                "状态码：" + commonData.getStatusCode() + "\n" +
                                "状态码描述信息：" + commonData.getStatusCode().getDescription() + "\n" +
                                "服务器提示信息：" + commonData.getTip()
                        );
                    }

                }
            }
        });
    }
}


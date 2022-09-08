package com.zheolls.zenfacedigit.network.helpers.upload;

import android.util.Log;

import androidx.annotation.NonNull;

import com.zheolls.zenfacedigit.network.StatusCode;
import com.zheolls.zenfacedigit.network.beans.CommonBean;
import com.zheolls.zenfacedigit.network.beans.CommonListBean;
import com.zheolls.zenfacedigit.network.helpers.OkhttpHelper;
import com.zheolls.zenfacedigit.network.helpers.request.DataBack;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadHelper extends OkhttpHelper {
    // 使用Gson解析响应时的依据
    private Class dataBackClass = null;

    /**
     * 构造一个请求帮助类
     *
     * @param url    请求的url
     * @param client OkHttpClient，用于发起请求
     */
    public UploadHelper(String url, OkHttpClient client) {
        super(url, client);
        fileLists = new HashMap<>();
    }

    /**
     * 设置 Gson 解析响应内容构造对象时使用的类
     * 传入 Object.class 类字面量，类似Activity跳转时的第二个参数
     */
    public <T> void setDataBackClass(Class<T> dataBackClass) {
        this.dataBackClass = dataBackClass;
    }

    /**
     * 添加一个文件
     * <p>
     * 可以通过 多次调用 添加 多个文件
     * 既可以用于上传单个文件时，也可以用于逐个添加不同 fileTag 的文件
     *
     * @param fileTag 文件辨识符，请使用 network.applications 包中定义的常量
     * @param file    要上传的文件，请确保该文件存在
     */
    public void addFile(String fileTag, File file) {
        if (!fileLists.containsKey(fileTag)) {
            fileLists.put(fileTag, new ArrayList<>());
        }
        Objects.requireNonNull(fileLists.get(fileTag)).add(file);
    }

    /**
     * 添加多个具有相同 文件辨识符 的文件
     * <p>
     * 可以通过 多次调用 添加具有 不同文件辨识符 的 多组 文件
     *
     * @param fileTag 文件辨识符，请使用 network.applications 包中定义的常量
     * @param files   要上传的文件，请确保这些文件均存在
     */
    public void addFiles(String fileTag, File[] files) {
        if (!fileLists.containsKey(fileTag)) {
            fileLists.put(fileTag, new ArrayList<>());
        }
        Objects.requireNonNull(fileLists.get(fileTag)).addAll(Arrays.asList(files));
    }

    /**
     * 构造 post 文件的请求
     * 将会把所有通过 addFile 和 addFiles 方法添加的文件全部添加到 请求中
     * 也会把所有使用 addPostParam 添加的参数参加到请求中
     * 当你没有添加任何文件时将会抛出 NoFileAddedException 异常
     */
    private Request postFileRequestBuilder() throws FileCountException {
        if (getFileCount() == 0) {
            throw new FileCountException("请先添加文件再调用 do... 方法！");
        }
        // 构造请求表单
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (Map.Entry<String, ArrayList<File>> entry : fileLists.entrySet()) {
            for (File file : entry.getValue()) {
                builder.addFormDataPart(
                        entry.getKey(),
                        file.getName(),
                        RequestBody.create(
                                MediaType.parse("multipart/from-data"),
                                file));
            }
        }
        // 添加请求参数
        builder.addFormDataPart("parameters", getPostParamsString());
        // 构造请求
        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();

        return request;
    }

    /**
     * 使用 post 请求上传文件，同时附加通过 addPostParam 添加的参数
     * 当你没有添加任何文件时将会抛出 NoFileAddedException 异常
     *
     * @param deleteOnSuccess 是否在上传成功之后删除文件
     *                        - true: 删除
     *                        - false: 不删除
     * @param dataBack        定义在收到响应时如何处理数据的接口
     */
    private <T> void postFileBase(final boolean deleteOnSuccess, final DataBack<T> dataBack)
            throws FileCountException {
        final Call call = client.newCall(postFileRequestBuilder());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                final String message = e.getClass().getSimpleName().equals("ConnectException") ?
                        "连接服务器失败" : "未知连接错误";
                Log.e(TAG, "POST文件请求失败：" + e.getMessage() + ", url: " + url);

                // 输出异常调用栈
                e.printStackTrace();
                dataBack.onFailure(message);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
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
                    CommonBean commonData = gson.fromJson(responseContent, CommonBean.class);
                    final StatusCode responseCodeEnum = commonData.getStatusCode();
                    // 检查是否需要删除文件
                    if (responseCodeEnum == StatusCode.Ok) {
                        Log.i(TAG, "上传文件成功");
                        if (deleteOnSuccess) {
                            final int fileCount = getFileCount();
                            final int deletedFileCount = deleteAllFiles();
                            Log.i(TAG, fileCount == deletedFileCount ?
                                    "删除所有文件成功" :
                                    (fileCount - deletedFileCount) + "个文件删除失败");
                        }
                    }
                    // 打日志
                    else {
                        Log.e(TAG, "上传文件时遇到了问题");
                        // 基于不同的状态码输出不同的日志信息
                        switch (responseCodeEnum) {
                            case FileSaveError:
                            case NoFileBodyError:
                            case RequestMethodError:
                            case Unknown:
                                Log.e(TAG, "状态码：" + responseCode +
                                        "，提示信息：" + commonData.getTip() +
                                        "，状态码描述信息：" + responseCodeEnum.getDescription());
                                break;
                            default:
                                Log.e(TAG, "未知错误，状态码：" + responseCode);
                                jumpToDebugActivity();
                                break;
                        }
                    }

                    // 构造响应结果
                    T responseData = (T) gson.fromJson(responseContent, dataBackClass);
                    dataBack.onResponse(responseData);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    try {
                        CommonBean commonDataOnFailure = gson.fromJson(responseContent, CommonBean.class);
                        Log.e(TAG, "解析数据失败：\n" +
                                "状态码：" + commonDataOnFailure.getStatusCode() + "\n" +
                                "状态码描述信息：" + commonDataOnFailure.getStatusCode().getDescription() + "\n" +
                                "服务器提示信息：" + commonDataOnFailure.getTip()
                        );
                    }
                    catch (JsonSyntaxException jsonSyntaxException) {
                        CommonListBean commonDataOnFailure = gson.fromJson(responseContent, CommonListBean.class);
                        Log.e(TAG, "解析数据失败：\n" +
                                "状态码：" + commonDataOnFailure.getStatusCode() + "\n" +
                                "状态码描述信息：" + commonDataOnFailure.getStatusCode().getDescription() + "\n" +
                                "服务器提示信息：" + commonDataOnFailure.getTip()
                        );
                    }
                    dataBack.onFailure("解析数据失败");
                }
            }
        });
    }

    /**
     * 上传 一个 文件，同时附加通过 addPostParam 添加的参数
     * 当你添加的文件个数不是 1个 的时候，将会抛出 OneFileExpected 异常
     * 当你没有添加任何文件时将会抛出 NoFileAddedException 异常
     *
     * @param deleteOnSuccess 是否在上传成功之后删除文件
     *                        - true: 删除
     *                        - false: 不删除
     * @param dataBack        在上传成功之后处理上传响应结果
     */
    public <T> void doPostOneFile(final boolean deleteOnSuccess, final DataBack<T> dataBack)
            throws FileCountException {
        Log.i(TAG, "doPostOneFile: " + url);
        if (getFileCount() != 1) {
            throw new FileCountException("该方法只能post一个文件，请保证添加且仅添加了一个文件!");
        }
        postFileBase(deleteOnSuccess, dataBack);
    }

    /**
     * 一次性上传 多个 文件，同时附加通过 addPostParam 添加的参数
     * 当你没有添加任何文件时将会抛出 NoFileAddedException 异常
     *
     * @param deleteOnSuccess 是否在上传成功之后删除文件
     *                        - true: 删除
     *                        - false: 不删除
     * @param dataBack        在上传成功之后处理上传响应结果
     */
    public <T> void doPostFiles(final boolean deleteOnSuccess, final DataBack<T> dataBack)
            throws FileCountException {
        Log.i(TAG, "doPostFiles: " + url);
        postFileBase(deleteOnSuccess, dataBack);
    }

    /**
     * 调用上传文件的接口时若没有添加文件或文件数量与接口行为不匹配将会抛出这个异常
     */
    public static class FileCountException extends Exception {
        public FileCountException() {
            super();
        }

        public FileCountException(String message) {
            super(message);
        }
    }
}

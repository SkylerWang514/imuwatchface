package com.zheolls.zenfacedigit.network.helpers.download;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.zheolls.zenfacedigit.MyApplication;
import com.zheolls.zenfacedigit.network.StatusCode;
import com.zheolls.zenfacedigit.network.helpers.OkhttpHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 从服务器下载文件时帮助类
 */
public class DownloadHelper extends OkhttpHelper {
    /**
     * 构造一个请求帮助类
     *
     * @param url    请求的url
     * @param client OkHttpClient，用于发起请求
     */
    public DownloadHelper(final String url, final OkHttpClient client) {
        super(url, client);
    }

    /**
     * 执行下载动作
     *
     * @param saveDirectory    将该文件保存在何处
     * @param saveFilename     保存的文件名，传入 空串 将使用文件原名称
     * @param downloadListener 定义在不同的下载状态时执行何种操作
     */
    private void downloadBase(final String saveDirectory, final String saveFilename,
                              final OnDownloadListener downloadListener) {
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json;charset=utf-8"),
                getPostParamsString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        final Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                final String message = e.getClass().getSimpleName().equals("ConnectException") ?
                        "连接服务器失败" : "未知连接错误";
                Log.e(TAG, "下载文件请求失败：" + e.getMessage() + ", url: " + url);

                // 输出异常调用栈
                e.printStackTrace();
                downloadListener.onFailure(message);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                // 响应状态码
                final int responseCode = response.code();
                // 如果下载失败则跳转到调试活动
                if (!response.isSuccessful()) {
                    Log.e(TAG, "状态码：" + responseCode);
                    try {
                        responseContent = Objects.requireNonNull(response.body()).string();
                    } catch (IOException e) {
                        responseContent = "遇到IO异常，获取响应内容失败";
                        e.printStackTrace();
                    }
                    downloadListener.onFailure("请求失败，状态码：" + responseCode);
                    jumpToDebugActivity();
                    return;
                }

                StatusCode statusCode = StatusCode.fromValue(responseCode);
                // 正常响应而没有正常下载，处理之后直接返回
                if (statusCode != StatusCode.Ok) {
                    Log.e(TAG, "下载失败，状态码：" + statusCode.getStatusCodeValue() +
                            "，描述信息：" + statusCode.getDescription());
                    downloadListener.onFailure(statusCode.getDescription());
                    return;
                }

                // 成功下载文件，开始保存文件
                InputStream inputStream = null;
                // 流式读取响应内容时的缓冲区
                byte[] buffer = new byte[2048];
                FileOutputStream outputStream = null;
                Log.i(TAG, "文件保存目录：" + saveDirectory);

                try {
                    // 获取响应的流式内容
                    inputStream = Objects.requireNonNull(response.body()).byteStream();
                    // 获取文件大小
                    long contentLength = response.body().contentLength();
                    // 打开输出文件
                    File file = new File(saveDirectory, saveFilename.isEmpty() ?
                            getHeaderFileName(response) : saveFilename);
                    // 获取文件的绝对路径，在保存成功时返回给调用者
                    final String fileAbsolutePath = file.getAbsolutePath();
                    Log.i(TAG, "文件保存路径：" + file.getAbsolutePath());

                    // 逐块写入文件中
                    outputStream = new FileOutputStream(file);
                    // 已保存的进度
                    long savedLength = 0;
                    // 从输入流中读取出的数据长度
                    int readLength = 0;
                    while ((readLength = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, readLength);
                        savedLength += readLength;
                        // finalSavedLength 是为了能在主线程中运行进行的妥协变量
                        long finalSavedLength = savedLength;
                        downloadListener.onProgress((int) (finalSavedLength * 1.0f / contentLength * 100));

                    }
                    outputStream.flush();
                    // 下载完成
                    downloadListener.onSuccess(fileAbsolutePath);
                } catch (Exception e) {
                    e.printStackTrace();
                    downloadListener.onFailure("保存到本地时失败");
                    Log.e(TAG, "保存到本地时遇到问题：" + e.getMessage());
                } finally {
                    // 关闭输入输出流
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "保存文件时，关闭输入输出流失败");
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    /**
     * 执行下载动作
     * 将下载完成的文件保存到应用的 data 文件夹，该文件夹对用户不可见
     *
     * @param downloadListener 定义在不同的下载状态时执行何种操作
     */
    public void downloadToDataDir(final OnDownloadListener downloadListener) {
        Log.e(TAG, "downloadToDataDir: " + url);
        downloadBase(MyApplication.context.getFilesDir().getAbsolutePath(), "", downloadListener);
    }

    /**
     * 执行下载动作
     * 将下载完成的文件保存到应用的 data 文件夹，该文件夹对用户不可见
     *
     * @param saveFilename     保存为的文件名
     * @param downloadListener 定义在不同的下载状态时执行何种操作
     */
    public void downloadToDataDirWithName(final String saveFilename, final OnDownloadListener downloadListener) {
        Log.e(TAG, "downloadToDataDirWithName: " + url);
        downloadBase(MyApplication.context.getFilesDir().getAbsolutePath(), saveFilename, downloadListener);
    }

    /**
     * 执行下载动作
     * 将下载完成的文件保存到应用的外部 data 文件夹，该文件夹在用户获取访问权限后可见
     *
     * @param downloadListener 定义在不同的下载状态时执行何种操作
     */
    public void downloadToExternalDataDir(final OnDownloadListener downloadListener) {
        Log.e(TAG, "downloadToExternalDataDir: " + url);
        downloadBase(MyApplication.context.getExternalFilesDir("").getAbsolutePath(), "", downloadListener);
    }

    /**
     * 执行下载动作
     * 将下载完成的文件保存到应用的外部 data 文件夹，该文件夹在用户获取访问权限后可见
     *
     * @param saveFilename     保存为的文件名
     * @param downloadListener 定义在不同的下载状态时执行何种操作
     */
    public void downloadToExternalDataDirWithName(final String saveFilename, final OnDownloadListener downloadListener) {
        Log.e(TAG, "downloadToExternalDataDirWithName: " + url);
        downloadBase(MyApplication.context.getExternalFilesDir("").getAbsolutePath(), saveFilename, downloadListener);
    }

    /**
     * 执行下载动作
     * 将下载完成的文件保存到指定的目录下
     *
     * @param saveDirectory    将该文件保存在何处
     * @param downloadListener 定义在不同的下载状态时执行何种操作
     */
    public void downloadToDir(final String saveDirectory, final OnDownloadListener downloadListener) {
        Log.e(TAG, "downloadToDir: " + url);
        downloadBase(saveDirectory, "", downloadListener);
    }

    /**
     * 执行下载动作
     * 将下载完成的文件保存到指定的目录下
     *
     * @param saveDirectory    将该文件保存在何处
     * @param saveFilename     保存为的文件名
     * @param downloadListener 定义在不同的下载状态时执行何种操作
     */
    public void downloadToDirWithName(final String saveDirectory, final String saveFilename, final OnDownloadListener downloadListener) {
        Log.e(TAG, "downloadToDirWithName: " + url);
        downloadBase(saveDirectory, saveFilename, downloadListener);
    }

    /**
     * 通过解析响应头获取服务器发回的文件的名称
     *
     * @param response 服务器发回的响应
     */
    private String getHeaderFileName(Response response) {
        String dispositionHeader = response.header("Content-Disposition");
        if (!TextUtils.isEmpty(dispositionHeader)) {
            dispositionHeader.replace("attachment;filename=", "");
            dispositionHeader.replace("filename*=utf-8", "");
            String[] strings = dispositionHeader.split("; ");
            if (strings.length > 1) {
                dispositionHeader = strings[1].replace("filename=", "");
                dispositionHeader = dispositionHeader.replace("\"", "");
                return dispositionHeader;
            }
        }
        return "";
    }
}

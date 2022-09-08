package com.zheolls.zenfacedigit.network.helpers.download;

public interface OnDownloadListener {
    /**
     * 下载成功时执行的操作
     *
     * @param fileAbsolutePath 文件的保存到的绝对路径
     */
    void onSuccess(final String fileAbsolutePath);

    /**
     * 下载失败时执行的何种操作
     *
     * @param message 下载失败时的提示信息
     */
    void onFailure(final String message);

    /**
     * 下载进度
     * 100以内的整数，100表示完全下载成功
     *
     * @param progress 进度
     */
    void onProgress(final int progress);
}

package me.chaozhouzhang.okhttplearning.download;

/**
 * Created on 2019/3/12 17:20
 *
 * @author zhangchaozhou
 */
public interface DownloadListener {


    /**
     * 下载进度
     *
     * @param progress
     */
    void onProgress(int progress);


    /**
     * 已经暂停
     */
    void onPaused();


    /**
     * 已经取消
     */
    void onCanceled();


    /**
     * 已经成功
     */
    void onSucceed();


    /**
     * 已经失败
     */
    void onFailed();
}

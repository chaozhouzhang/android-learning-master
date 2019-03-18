package me.chaozhouzhang.okhttplearning.download;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

/**
 * Created on 2019/3/12 16:23
 *
 * @author zhangchaozhou
 */
public class DownloadService extends Service {


    private String mDownloadUrl;
    private DownloadTask mDownloadTask;


    private DownloadListener mDownloadListener = new DownloadListener() {
        @Override
        public void onProgress(int progress) {

            System.out.println(""+progress);
        }

        @Override
        public void onPaused() {

            System.out.println("onPaused");
        }

        @Override
        public void onCanceled() {
            System.out.println("onCanceled");
        }

        @Override
        public void onSucceed() {
            System.out.println("onSucceed");
        }

        @Override
        public void onFailed() {
            System.out.println("onFailed");
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new DownloadBinder();
    }


    public class DownloadBinder extends Binder {


        public void startDownload(String url) {
            mDownloadUrl = url;
            if (mDownloadTask == null) {
                mDownloadTask = new DownloadTask(mDownloadListener);
                mDownloadTask.execute(mDownloadUrl);
            }
        }


        public void pauseDownload() {
            if (mDownloadTask == null) {
                mDownloadTask.pauseDownload();
            }
        }


        /**
         * 取消下载
         */
        public void cancelDownload() {
            if (mDownloadTask != null) {
                mDownloadTask.cancelDownload();
            }
        }


    }
}

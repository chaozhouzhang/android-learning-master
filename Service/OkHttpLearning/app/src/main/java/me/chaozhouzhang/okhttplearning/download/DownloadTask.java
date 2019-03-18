package me.chaozhouzhang.okhttplearning.download;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created on 2019/3/12 16:31
 *
 * @author zhangchaozhou
 */
public class DownloadTask extends AsyncTask<String, Integer, DownloadTask.Status> {


    public enum Status {
        /**
         * 下载成功状态
         */
        STATUS_SUCCESS,
        /**
         * 下载失败状态
         */
        STATUS_FAILED,
        /**
         * 下载暂停状态
         */
        STATUS_PAUSED,
        /**
         * 下载取消状态
         */
        STATUS_CANCELED
    }



    private boolean isCanceled = false;

    private boolean isPaused = false;

    private int lastProgress;

    /**
     * 下载监听
     */
    private DownloadListener mDownloadListener;


    public DownloadTask(DownloadListener downloadListener) {
        mDownloadListener = downloadListener;
    }

    @Override
    protected Status doInBackground(String... params) {
        InputStream is = null;
        RandomAccessFile savedFile = null;
        File file = null;
        //记录已经下载的文件长度
        long downloadLength = 0;
        //文件下载地址
        String downloadUrl = params[0];
        //下载文件的名称
        String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
        //下载文件存放的目录
        String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        //创建一个文件
        file = new File(directory + fileName);
        if (file.exists()) {
            //如果文件存在的话，得到文件的大小
            downloadLength = file.length();
        }
        //得到下载内容的大小
        long contentLength = getContentLength(downloadUrl);
        if (contentLength == 0) {
            return Status.STATUS_FAILED;
        } else if (contentLength == downloadLength) {
            //已下载字节和文件总字节相等，说明已经下载完成了
            return Status.STATUS_SUCCESS;
        }
        OkHttpClient client = new OkHttpClient();
        /**
         * HTTP请求是有一个Header的，里面有个Range属性是定义下载区域的，它接收的值是一个区间范围，
         * 比如：Range:bytes=0-10000。这样我们就可以按照一定的规则，将一个大文件拆分为若干很小的部分，
         * 然后分批次的下载，每个小块下载完成之后，再合并到文件中；这样即使下载中断了，重新下载时，
         * 也可以通过文件的字节长度来判断下载的起始点，然后重启断点续传的过程，直到最后完成下载过程。
         */
        Request request = new Request.Builder()
                //断点续传要用到的，指示下载的区间
                .addHeader("RANGE", "bytes=" + downloadLength + "-")
                .url(downloadUrl)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response != null) {
                is = response.body().byteStream();
                savedFile = new RandomAccessFile(file, "rw");
                //跳过已经下载的字节
                savedFile.seek(downloadLength);
                byte[] b = new byte[1024];
                int total = 0;
                int len;
                while ((len = is.read(b)) != -1) {
                    if (isCanceled) {
                        return Status.STATUS_CANCELED;
                    } else if (isPaused) {
                        return Status.STATUS_PAUSED;
                    } else {
                        total += len;
                        savedFile.write(b, 0, len);
                        //计算已经下载的百分比
                        int progress = (int) ((total + downloadLength) * 100 / contentLength);
                        //注意：在doInBackground()中是不可以进行UI操作的，如果需要更新UI,比如说反馈当前任务的执行进度，
                        //可以调用publishProgress()方法完成。
                        publishProgress(progress);
                    }

                }
                response.body().close();
                return Status.STATUS_SUCCESS;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (savedFile != null) {
                    savedFile.close();
                }
                if (isCanceled && file != null) {
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Status.STATUS_FAILED;
    }


    /**
     * 得到下载内容的大小
     *
     * @param downloadUrl
     * @return
     */
    private long getContentLength(String downloadUrl) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(downloadUrl).build();
        try {
            Response response = client.newCall(request).execute();
            if (response != null && response.isSuccessful()) {
                long contentLength = response.body().contentLength();
                response.body().close();
                return contentLength;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }


    @Override
    protected void onPostExecute(Status status) {
        super.onPostExecute(status);
        switch (status){
            case STATUS_SUCCESS:
                mDownloadListener.onSucceed();
                break;
            case STATUS_FAILED:
                mDownloadListener.onFailed();
                break;
            case STATUS_PAUSED:
                mDownloadListener.onPaused();
                break;
            case STATUS_CANCELED:
                mDownloadListener.onCanceled();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        int progress=values[0];
        if(progress>lastProgress){
            mDownloadListener.onProgress(progress);
            lastProgress=progress;
        }
    }


    public void  pauseDownload(){
        isPaused=true;
    }

    public void cancelDownload(){
        isCanceled=true;
    }
}
package me.chaozhouzhang.avlearning;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created on 2019/3/13 19:49
 *
 * @author zhangchaozhou
 */
public class AvMediaPlayer extends MediaPlayer implements MediaPlayer.OnCompletionListener {
    public enum Status {
        /**
         * 媒体播放器状态
         */
        IDLE, INITIALIZED, STARTED, PAUSED, STOPPED, COMPLETED
    }


    private Status mStatus;


    private OnCompletionListener mOnCompletionListener;


    public Status getStatus() {
        return mStatus;
    }

    /**
     * 设置状态
     *
     * @param status
     * @return
     */
    public AvMediaPlayer setStatus(Status status) {
        mStatus = status;
        return this;
    }

    public AvMediaPlayer() {
        super();
        mStatus = Status.IDLE;
        super.setOnCompletionListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mStatus = Status.COMPLETED;
        if (mOnCompletionListener != null) {
            mOnCompletionListener.onCompletion(mp);
        }
    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, IllegalStateException, SecurityException {
        super.setDataSource(path);
        mStatus = Status.INITIALIZED;
    }

    @Override
    public void start() throws IllegalStateException {
        super.start();
        mStatus = Status.STARTED;
    }

    @Override
    public void stop() throws IllegalStateException {
        super.stop();
        mStatus = Status.STOPPED;
    }

    @Override
    public void pause() throws IllegalStateException {
        super.pause();
        mStatus = Status.PAUSED;
    }

    @Override
    public void reset() {
        super.reset();
        mStatus = Status.IDLE;
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        super.setOnCompletionListener(listener);
        this.mOnCompletionListener = listener;
    }


    /**
     * 是否完成
     *
     * @return
     */
    public boolean isComplete() {
        return mStatus == Status.COMPLETED;
    }

}

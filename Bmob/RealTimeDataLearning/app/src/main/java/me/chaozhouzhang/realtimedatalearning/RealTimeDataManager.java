package me.chaozhouzhang.realtimedatalearning;

/**
 * Created on 2019/3/14 13:17
 * <p>
 * 参考地址
 * 服务端：https://github.com/googollee/go-socket.io
 * 客户端：https://github.com/koush/android-websockets
 * 客户端已由参考的项目方法改为OkHttp
 *
 * @author zhangchaozhou
 */
public final class RealTimeDataManager {


    /**
     * 单例
     */
    private static RealTimeDataManager mInstance = null;


    /**
     * 私有
     */
    private RealTimeDataManager() {
    }


    /**
     * 单例
     *
     * @return
     */
    public static RealTimeDataManager getInstance() {
        if (mInstance == null) {
            mInstance = new RealTimeDataManager();
        }
        return mInstance;
    }


    /**
     * 开始监听
     *
     * @param realTimeDataListener
     */
    public void start(RealTimeDataListener realTimeDataListener) {
        Client client = new Client();
        client.connect(realTimeDataListener);
    }


}

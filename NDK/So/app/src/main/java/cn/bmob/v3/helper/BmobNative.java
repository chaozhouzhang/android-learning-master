package cn.bmob.v3.helper;

import android.content.Context;

/**
 * 本地方法
 *
 * @author smile
 * @class BmobNative
 * @date 2016-5-13 下午4:11:51
 */
public class BmobNative {

    static {
        try {
            System.loadLibrary("bmob");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化
     *
     * @param context
     */
    public static final native boolean init(Context context, String appid);

    /**
     * 获取appkey
     *
     * @return
     */
    public static final native String getAppId();


    public static String SECRET_KEY = "";

    /**
     * 保存secretKey
     *
     * @param key
     */
    public static final native void saveKey(String key);


    /**
     */
    public static final native String getSecretKey();


    /**
     * 是否保存过Key
     *
     * @return
     */
    public static final native boolean hasKey();

    /**
     * 保存间隔时间
     *
     * @param time
     */
    public static final native void saveInterval(String time);

    /**
     * 获取本机与服务器之间的间隔时间
     *
     * @return
     */
    public static final native String getInterval();

    /**
     * 获取acceptId
     *
     * @return
     */
    public static final native String getAcceptId();

    /**
     * @param agent
     * @param source
     * @return
     */
    public static final native String encrypt(String agent, String source);

    /**
     * @param responseId
     * @param source
     * @return
     */
    public static final native String decrypt(String responseId, String source);

    /**
     * @param source
     * @return
     */
    public static final native String encryptByKey(String source);

    /**
     * @param source
     * @return
     */
    public static final native String decryptByKey(String source);

    /**
     * 清除内存资源
     */
    public static final native void clear();


}

package me.chaozhouzhang.ndklearning;

/**
 * Created on 2019/3/7 15:47
 *
 * @author zhangchaozhou
 */
public class NdkJni {

    static {
        System.loadLibrary("NdkJni");
    }


    /**
     * Java代码调用本地代码
     *
     * @return
     */
    public native static String callNative();


    /**
     * 本地代码调用Java代码
     *
     * @param nativeValue
     */
    public void callJava(int nativeValue) {

    }
}

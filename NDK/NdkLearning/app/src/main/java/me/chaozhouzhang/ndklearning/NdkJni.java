package me.chaozhouzhang.ndklearning;

import android.util.Log;

/**
 * Created on 2019/3/7 15:47
 *
 * @author zhangchaozhou
 */
public abstract class NdkJni {
    private int number;
    private Person person;


    static {
        try {
            System.loadLibrary("NdkJni");
        } catch (Exception e) {
            //TODO :handle exception

        }
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

        Log.e("callJava", "nativeValue" + nativeValue);
    }


    public NdkJni() {
        person = new Person();
        number = getNumber();
        callNativeInitialPerson(person);
    }

    /**
     * 获取数字
     *
     * @return
     */
    abstract protected int getNumber();

    private native void callNativeInitialPerson(Object person);
}

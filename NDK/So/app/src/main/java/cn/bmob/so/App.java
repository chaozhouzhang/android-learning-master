package cn.bmob.so;

import android.app.Application;

import cn.bmob.v3.helper.BmobNative;

/**
 * Created on 2019/3/13 10:40
 *
 * @author zhangchaozhou
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        boolean result = BmobNative.init(this, "appid保存测试");
        System.out.println(result);
    }
}

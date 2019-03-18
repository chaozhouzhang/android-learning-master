package me.chaozhouzhang.realtimedatalearning;

import android.app.Application;

import org.json.JSONObject;

/**
 * Created on 2019/3/15 16:17
 *
 * @author zhangchaozhou
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RealTimeDataManager.getInstance().start(new RealTimeDataListener() {
            @Override
            public void onConnectCompleted(Client client, Exception ex) {
                System.err.println(client.toString());
                if (ex == null) {
                    System.out.println("APP=onConnectCompleted：连接成功");
                    client.subTableUpdate("Category");
                } else {
                    System.out.println("APP=onConnectCompleted：连接失败" + ex.getMessage());
                }
            }

            @Override
            public void onDataChange(Client client, JSONObject data) {
                System.err.println(client.toString());
                System.out.println("APP=最终收到消息：" + data.toString());
            }
        });
    }
}

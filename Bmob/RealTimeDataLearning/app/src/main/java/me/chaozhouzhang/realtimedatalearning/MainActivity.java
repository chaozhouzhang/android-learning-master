package me.chaozhouzhang.realtimedatalearning;

import android.os.Bundle;
import android.view.View;

import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author zhangchaozhou
 */
public class MainActivity extends AppCompatActivity {


    public static final String APPID = "d59c62906f447317e41cea2fe47ef856";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }


    public void start(View view) {
        RealTimeDataManager.getInstance().start(new RealTimeDataListener() {
            @Override
            public void onConnectCompleted(Client client, Exception ex) {
                System.err.println(client.toString());
                if (ex == null) {
                    System.out.println("onConnectCompleted：连接成功");
                    client.subTableUpdate("Category");
                } else {
                    System.out.println("onConnectCompleted：连接失败" + ex.getMessage());
                }
            }

            @Override
            public void onDataChange(Client client, JSONObject data) {
                System.err.println(client.toString());
                System.out.println("最终收到消息：" + data.toString());
            }
        });
    }
}

package cn.bmob.so;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import cn.bmob.v3.helper.BmobNative;

/**
 * @author zhangchaozhou
 */
public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("获取appid："+BmobNative.getAppId());
        System.out.println("是否存在key："+BmobNative.hasKey());
        BmobNative.saveKey("secretKey保存测试");
        System.out.println("是否存在key："+BmobNative.hasKey());
        System.out.println("获取secretKey："+BmobNative.getSecretKey());


        BmobNative.saveInterval("internal保存测试");
        System.out.println("获取internal："+BmobNative.getInterval());

        Cmob.secret(this,"12784168944a56ae41c4575686b7b332");
    }

}

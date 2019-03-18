package cn.bmob.so;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created on 2019/3/5 11:33
 *
 * @author zhangchaozhou
 */
public class CmobServiceManager {


    /**
     * 请求的地址
     */
    public static final String DEFAULT_URL = "http://open2.bmob.cn/8/";


    /**
     * 超时时间 5s
     */
    private static final int DEFAULT_READ_TIME_OUT = 10;
    private static final int DEFAULT_TIME_OUT = 5;
    private Retrofit mRetrofit;

    private CmobServiceManager() {
        // 创建 OKHttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //连接超时时间
        builder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);
        //写操作 超时时间
        builder.writeTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);
        //读操作超时时间
        builder.readTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);

        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request()
                        .newBuilder()
                        .addHeader("Content-Type", "text/plain; charset=utf-8")
                        .build();
                return chain.proceed(request);
            }
        });
        // 创建Retrofit
        mRetrofit = new Retrofit.Builder()
                .client(builder.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(DEFAULT_URL)
                .build();
    }

    private static class SingletonHolder {
        private static final CmobServiceManager INSTANCE = new CmobServiceManager();
    }

    /**
     * 获取CmobServiceManager
     *
     * @return
     */
    public static CmobServiceManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 获取对应的Service
     *
     * @param service Service 的 class
     * @param <T>
     * @return
     */
    public <T> T create(Class<T> service) {
        return mRetrofit.create(service);
    }

}

package cn.bmob.so;


import android.content.Context;
import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import androidx.annotation.RequiresApi;
import cn.bmob.v3.helper.BmobNative;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Response;

import static cn.bmob.so.Utils.getCombinedDeviceID;

/**
 * Created on 2019/3/5 10:58
 *
 * @author zhangchaozhou
 */
public class Cmob {


    public static void secret(Context context, String appKey)  {


        JsonObject body = new JsonObject();
        body.addProperty("appKey", appKey);
        body.addProperty("appSign", Utils.getAppSign(context));
        JsonObject client = new JsonObject();
        client.addProperty("caller", "Android");
        JsonObject ex = new JsonObject();
        ex.addProperty("version", Build.VERSION.RELEASE);
        ex.addProperty("package", context.getPackageName());
        ex.addProperty("uuid", getCombinedDeviceID(context));
        client.add("ex", ex);
        body.add("client", client);
        body.addProperty("v", "v3.6.9");
        Request.Builder builder = new Request.Builder();
        String agent = Utils.getUserAgent(context);
        builder.header("Content-Type", "text/plain; charset=utf-8")
                //替换同名请求头参数
//                .header("Accept-Encoding", "gzip,deflate,sdch")
                .header("User-Agent", agent)
                .url(CmobServiceManager.DEFAULT_URL + "secret");
        String json = new Gson().toJson(body);

        System.out.println(json);
        System.out.println(agent);

//        String key1 = agent.substring(agent.length() - 16);
//        System.out.println("key1:" + key1);
//        String data1 = Utils.encrypt(key1, key1, json);


        String data1 = BmobNative.encrypt(agent,json);
        System.out.println(data1);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), data1);


        OkHttpClient okHttpClient = new OkHttpClient();
        builder.post(requestBody);
        Request request = builder.build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                System.err.println(e.getMessage());
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {


                if (response.isSuccessful()) {
                    String he = response.header("Response-Id");
                    System.out.println(he);
                    String res = response.body().string();
                    System.out.println(res);

//                    String key2 = he.substring(he.length() - 16);
//                    System.out.println("key2:" + key2);
//                    System.out.println("response：" + res);
//                    System.out.println(Utils.decrypt(key2, key2, res));

                    System.out.println(BmobNative.decrypt(he,res));
                } else {
                    System.err.println(response.message());
                }

            }
        });


    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void secretRetrofit(Context context, String appKey) throws UnsupportedEncodingException {


        JsonObject body = new JsonObject();
        body.addProperty("appKey", appKey);
        body.addProperty("appSign", Utils.getAppSign(context));
        JsonObject client = new JsonObject();
        client.addProperty("caller", "Android");
        JsonObject ex = new JsonObject();
        ex.addProperty("version", Build.VERSION.RELEASE);
        ex.addProperty("package", context.getPackageName());
        ex.addProperty("uuid", getCombinedDeviceID(context));
        client.add("ex", ex);
        body.add("client", client);
        body.addProperty("v", "v3.6.9");
        String agent = Utils.getUserAgent(context);
        String json = new Gson().toJson(body);

        System.out.println(json);
        System.out.println(agent);

        String key1 = agent.substring(agent.length() - 16);

        System.out.println("key1:" + key1);

        String data1 = Utils.encrypt(key1, key1, json);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), data1);


        CmobService cmobService = CmobServiceManager.getInstance().create(CmobService.class);


        retrofit2.Call<ResponseBody> stringCall = cmobService.secret(agent, requestBody);
        stringCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String he = response.headers().get("Response-Id");
                    if (he == null) {
                        return;
                    }

                    System.out.println(he);

                    String key2 = he.substring(he.length() - 16);
                    System.out.println("key2:" + key2);

                    String res = null;
                    try {
                        res = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("response：" + res);
                    System.out.println(Utils.decrypt(key2, key2, res));
                } else {
                    System.err.println(response.message());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                System.err.println(t.getMessage());
            }
        });


    }


}

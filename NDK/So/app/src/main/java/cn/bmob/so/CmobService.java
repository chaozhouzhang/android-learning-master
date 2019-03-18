package cn.bmob.so;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created on 2019/3/5 11:20
 *
 * @author zhangchaozhou
 */
public interface CmobService {


    /**
     * 获取secret key
     * <p>
     * url:
     * http://open2.bmob.cn/8/secret
     * <p>
     * header:
     * User-Agent:应用包名/应用版本号时间戳毫秒数平台名称SDK版本号
     * me.chaozhouzhang.cmobsdk/11552284931240Androidv3.6.9
     *
     * @param userAgent
     * @param body
     * @return
     */
    @POST("secret")
    Call<ResponseBody> secret(@Header("User-Agent") String userAgent, @Body RequestBody body);


}

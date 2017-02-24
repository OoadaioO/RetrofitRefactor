package cc.kaipao.dongjia.http;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by xb on 17/2/21.
 */

public interface RetrofitHttpService {

    // @Url 替换url
    // @QueryMap  替换url中查询参数
    // @Header  替换header
    // @FieldMap 替换post请求body中参数
    // @FormUrlEncoded post请求需要加的方法注解
    // @POST() 标示该方法为post请求
    // @GET() 标示该方法为get请求


    @GET()
    Call<String> get(@HeaderMap Map<String, String> headers, @Url String url, @QueryMap Map<String, String> params);


    @FormUrlEncoded
    @POST()
    Call<String> post(@HeaderMap Map<String, String> headers, @Url String url, @FieldMap Map<String, String> params);



    @Streaming
    @GET()
    Call<ResponseBody> download(@HeaderMap Map<String, String> headers, @Url String url, @QueryMap Map<String, String> params);


}

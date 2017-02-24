package cc.kaipao.dongjia.http.utils;

import android.content.Context;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cc.kaipao.dongjia.http.BuildConfig;
import cc.kaipao.dongjia.http.interceptor.RedirectInterceptor;
import okhttp3.OkHttpClient;

/**
 * Created by xb on 17/2/21.
 */

public class OkHttpProvider {
    static OkHttpClient okHttpClient;

    public static OkHttpClient okHttpClient(final Context context, final String BASE_URL, final List<String> SERVER_KEYS) {

        if (okHttpClient == null) {
            synchronized (OkHttpProvider.class) {
                if (okHttpClient == null) {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .addInterceptor(new RedirectInterceptor(BASE_URL, SERVER_KEYS))
                            .connectTimeout(45, TimeUnit.SECONDS)
                            .readTimeout(45, TimeUnit.SECONDS)
                            .writeTimeout(1, TimeUnit.MINUTES)
                            .build();

                    if (BuildConfig.DEBUG) {
                        // TODO: 17/2/21
                    }
                    okHttpClient = client;
                }
            }
        }

        return okHttpClient;
    }
}

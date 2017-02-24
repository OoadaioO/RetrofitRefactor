package cc.kaipao.dongjia.http.interceptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.validation.Validator;

import cc.kaipao.dongjia.http.HttpUtil;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by xb on 17/2/21.
 */

public class RedirectInterceptor implements Interceptor {

    String mBaseIp;
    List<String> mRedirectHeaders;

    public RedirectInterceptor(String mBaseIp, String[] mRedirectHeaders) {
        this(mBaseIp, Arrays.asList(mRedirectHeaders));
    }

    public RedirectInterceptor(String mBaseIp, List<String> mRedirectHeaders) {
        this.mBaseIp = mBaseIp;
        this.mRedirectHeaders = mRedirectHeaders;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        String replaceKey = null;
        if (mRedirectHeaders != null && !mRedirectHeaders.isEmpty()) {
            for (String key : mRedirectHeaders) {
                if (!HttpUtil.checkNULL(request.header(key))) {
                    if (replaceKey != null) {
                        throw new RuntimeException(String.format("Duplicated Redirect url:%s and url :%s", key, replaceKey));
                    }
                    replaceKey = key;
                }
            }
        }

        if (HttpUtil.checkNULL(replaceKey)) {
            return chain.proceed(request);
        }

        String redirectUrl = request.header(replaceKey);
        String url = request.url().toString();
        url = url.replace(mBaseIp, redirectUrl);
        Request newRequest = request.newBuilder().url(url).build();
        return chain.proceed(newRequest);
    }

}

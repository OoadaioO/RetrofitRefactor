package cc.kaipao.dongjia.http;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.kaipao.dongjia.http.converter.StringConverterFactory;
import cc.kaipao.dongjia.http.exception.NetworkException;
import cc.kaipao.dongjia.http.interfaces.ErrorCallBack;
import cc.kaipao.dongjia.http.interfaces.HeadersInterceptor;
import cc.kaipao.dongjia.http.interfaces.ParamsInterceptor;
import cc.kaipao.dongjia.http.interfaces.ProgressCallback;
import cc.kaipao.dongjia.http.interfaces.SuccessCallback;
import cc.kaipao.dongjia.http.rxjava.SchedulersAdapter;
import cc.kaipao.dongjia.http.utils.OkHttpProvider;
import cc.kaipao.dongjia.http.utils.WriteFileUtil;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by xb on 17/2/21.
 */

public class HttpUtil {

    public static final int CODE_SUCCESS = 0;
    protected static volatile HttpUtil mInstance;
    protected static volatile RetrofitHttpService mService;
    protected static String mBaseUrl;
    protected ParamsInterceptor mParamsInterceptor;
    protected HeadersInterceptor mHeadersInterceptor;

    public HttpUtil(RetrofitHttpService mService, String mBaseUrl, ParamsInterceptor mParamsInterceptor, HeadersInterceptor mHeadersInterceptor) {
        this.mService = mService;
        this.mBaseUrl = mBaseUrl;
        this.mParamsInterceptor = mParamsInterceptor;
        this.mHeadersInterceptor = mHeadersInterceptor;
    }

    public static RetrofitHttpService getService() {
        if (mInstance == null) {
            throw new NullPointerException("HttpUtil has not be initialized!");
        }
        return mService;
    }


    /**
     * 初始化构造器
     */
    public static class SingletonBuilder {
        protected Context applicationContext;
        protected String baseUrl;
        protected List<String> serverKyes = new ArrayList<>();
        protected ParamsInterceptor paramsInterceptor;
        protected HeadersInterceptor headersInterceptor;
        protected List<Converter.Factory> converterFactories = new ArrayList<>();
        protected List<CallAdapter.Factory> adapterFactories = new ArrayList<>();
        OkHttpClient client;


        public SingletonBuilder(Context context) {
            try {
                //防止传入的是activity的上下文
                if (context instanceof Activity) {
                    applicationContext = context.getApplicationContext();
                } else {
                    applicationContext = context;
                }
            } catch (Exception e) {
                e.printStackTrace();
                applicationContext = context;
            }
        }

        public SingletonBuilder client(OkHttpClient client) {
            this.client = client;
            return this;
        }


        public SingletonBuilder paramsInterceptor(ParamsInterceptor interceptor) {
            this.paramsInterceptor = interceptor;
            return this;
        }

        public SingletonBuilder headersInterceptor(HeadersInterceptor headersInterceptor) {
            this.headersInterceptor = headersInterceptor;
            return this;
        }

        public SingletonBuilder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public SingletonBuilder addServerKey(String key) {
            this.serverKyes.add(key);
            return this;
        }

        public SingletonBuilder serverKeys(List<String> serverKyes) {
            this.serverKyes = serverKyes;
            return this;
        }

        public SingletonBuilder addConverterFactory(Converter.Factory factory) {
            this.converterFactories.add(factory);
            return this;
        }

        public SingletonBuilder addCallFactory(CallAdapter.Factory factory) {
            this.adapterFactories.add(factory);
            return this;
        }

        public HttpUtil build() {
            if (checkNULL(this.baseUrl)) {
                throw new NullPointerException("BASE_URL can not be null");
            }
            if (!"/".equals(baseUrl.substring(baseUrl.length() - 1, baseUrl.length()))) {
                baseUrl = baseUrl + "/";
            }

            if (converterFactories.isEmpty()) {
                converterFactories.add(StringConverterFactory.create());
            }
            if (adapterFactories.isEmpty()) {
                adapterFactories.add(RxJavaCallAdapterFactory.create());
            }

            if (client == null) {
                client = OkHttpProvider.okHttpClient(applicationContext, baseUrl, serverKyes);
            }

            Retrofit.Builder builder = new Retrofit.Builder();
            for (Converter.Factory convertFactory : converterFactories) {
                builder.addConverterFactory(convertFactory);
            }
            for (CallAdapter.Factory adapterFactory : adapterFactories) {
                builder.addCallAdapterFactory(adapterFactory);
            }
            Retrofit retrofit = builder.baseUrl(baseUrl)
                    .client(client).build();

            RetrofitHttpService retrofitHttpService = retrofit.create(RetrofitHttpService.class);
            mInstance = new HttpUtil(retrofitHttpService, baseUrl, paramsInterceptor, headersInterceptor);
            return mInstance;

        }


    }

    public static String addBaseUrl(String url) {
        if (checkNULL(mBaseUrl)) {
            throw new NullPointerException("can not add BaseUrl ,because of BaseUrl is null");
        }
        if (!url.contains(mBaseUrl)) {
            if (url.startsWith("/")) {
                return mBaseUrl + url.substring(1, url.length());
            }
            return mBaseUrl + url;
        }
        return url;
    }


    /**
     * 判断是否NULL
     */
    public static boolean checkNULL(String str) {
        return str == null || "null".equals(str) || "".equals(str);

    }

    public static Map<String, String> checkParams(Map<String, String> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        if (mInstance.mParamsInterceptor != null) {
            params = mInstance.mParamsInterceptor.checkParams(params);
        }
        List<String> removeKeys = new ArrayList<>();
        //retrofit的params的值不能为null，此处做下校验，防止出错
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (checkNULL(entry.getValue())) {
                removeKeys.add(entry.getKey());
            }
        }
        //移除null或空内容的参数
        for (String key : removeKeys) {
            params.remove(key);
        }

        return params;
    }


    public static Map<String, String> checkHeaders(Map<String, String> headers) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        if (mInstance.mHeadersInterceptor != null) {
            headers = mInstance.mHeadersInterceptor.checkHeaders(headers);
        }
        List<String> removeKeys = new ArrayList<>();

        //retrofit的headers的值不能为null，此处做下校验，防止出错
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (checkNULL(entry.getValue())) {
                removeKeys.add(entry.getKey());
            }
        }
        //移除null或空内容的参数
        for (String key : removeKeys) {
            headers.remove(key);
        }
        return headers;
    }


    // 判断是否NULL
    public static void Error(Context context, String msg) {
        if (checkNULL(msg)) {
            msg = "网络连接异常,请检查您的网络";
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static String message(String mes) {
        if (checkNULL(mes)) {
            mes = "网络连接异常,请检查您的网络";
        }
        return mes;
    }

    final static Map<String, Call> CALL_MAP = new HashMap<>();

    /*
        *添加某个请求
        *@author Administrator
        *@date 2016/10/12 11:00
        */
    protected static synchronized void putCall(Object tag, String url, Call call) {
        if (tag == null) {
            return;
        }
        synchronized (CALL_MAP) {
            CALL_MAP.put(tag.toString() + url, call);
        }
    }

    /*
    *取消某个界面都所有请求，或者是取消某个tag的所有请求
    * 如果要取消某个tag单独请求，tag需要转入tag+url
    *@author Administrator
    *@date 2016/10/12 10:57
    */
    public static synchronized void cancel(Object tag) {
        if (tag == null) {
            return;
        }
        List<String> list = new ArrayList<>();
        synchronized (CALL_MAP) {
            for (String key : CALL_MAP.keySet()) {
                if (key.startsWith(tag.toString())) {
                    CALL_MAP.get(key).cancel();
                    list.add(key);
                }
            }
        }
        for (String s : list) {
            removeCall(s);
        }

    }

    /*
    *移除某个请求
    *@author Administrator
    *@date 2016/10/12 10:58
    */
    protected static synchronized void removeCall(String url) {
        synchronized (CALL_MAP) {
            for (String key : CALL_MAP.keySet()) {
                if (key.contains(url)) {
                    url = key;
                    break;
                }
            }
            CALL_MAP.remove(url);
        }
    }


    /**
     * 接口构造器
     */
    public static class Builder<T> {

        Map<String, String> params = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        String url;
        String path;
        ErrorCallBack mErrorCallBack;
        SuccessCallback<T> mSuccessCallBack;
        ProgressCallback mProgressCallBack;
        Object tag;
        boolean rawString = false;

        TypeToken<T> token;

        public Builder<T> token(TypeToken<T> token) {
            this.token = token;
            return this;
        }


        public Builder<T> url(String url) {
            this.url = url;
            return this;
        }

        public Builder<T> savePath(String path) {
            this.path = path;
            return this;
        }

        public Builder<T> tag(Object tag) {
            this.tag = tag;
            return this;
        }

        public Builder<T> rawString(boolean rawString) {
            this.rawString = rawString;
            return this;
        }


        public Builder<T> params(Map<String, String> params) {
            this.params.putAll(params);
            return this;
        }

        public Builder<T> params(String key, String value) {
            this.params.put(key, value);
            return this;
        }

        public Builder<T> headers(Map<String, String> headers) {
            this.headers.putAll(headers);
            return this;
        }

        public Builder<T> header(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public Builder<T> success(SuccessCallback<T> success) {
            this.mSuccessCallBack = success;
            return this;
        }

        public Builder<T> progress(ProgressCallback progress) {
            this.mProgressCallBack = progress;
            return this;
        }

        public Builder<T> error(ErrorCallBack error) {
            this.mErrorCallBack = error;
            return this;
        }


        public Builder() {
            this(null);
        }

        public Builder(String url) {
            this.setParams(url);
        }


        protected void setParams(String url) {
            if (mInstance == null) {
                throw new NullPointerException("HttpUtil has not be initialized");
            }
            this.url = url;
            this.params = new HashMap<>();
            this.mErrorCallBack = new ErrorCallBack() {
                @Override
                public void error(NetworkException e) {

                }
            };

            this.mSuccessCallBack = new SuccessCallback<T>() {
                @Override
                public void success(T model) {

                }
            };

            this.mProgressCallBack = new ProgressCallback() {
                @Override
                public void progress(float p) {

                }
            };
        }

        protected String checkUrl(String url) {
            if (checkNULL(url)) {
                throw new NullPointerException("absolute url can not be empty");
            }

            if (!url.startsWith("http")) {
                url = mInstance.addBaseUrl(url);
            }

            return url;
        }


        /**
         * 异步调用
         */
        public void post() {
            this.url = checkUrl(this.url);
            Call<String> call = mService.post(checkHeaders(headers), url, checkParams(params));
            putCall(tag, this.url, call);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        onResult(response.body().toString());
                    } else {
                        onError(NetworkException.network());
                    }
                    removeCall();
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    onError(NetworkException.network());
                    removeCall();
                }
            });
        }

        /**
         * 同步调用
         */
        public void postSync() {
            this.url = checkUrl(this.url);
            Call<String> call = mService.post(checkHeaders(headers), this.url, checkParams(params));
            putCall(tag, this.url, call);
            try {

                Response<String> response = call.execute();
                if (response.isSuccessful()) {
                    onResult(response.body().toString());
                } else {
                    onError(NetworkException.network());
                }

            } catch (Exception e) {
                onError(NetworkException.network());
            } finally {
                removeCall();
            }
        }

        /**
         * 调用转换成rxjava<br></>
         * 请注意rxjava的调用是在同步线程中的,如果需要切线程,自行外部处理
         *
         * @return
         */
        public Observable<T> postRx() {
            return Observable.create(new Observable.OnSubscribe<T>() {
                @Override
                public void call(final Subscriber<? super T> subscriber) {
                    success(new SuccessCallback<T>() {
                        @Override
                        public void success(T bean) {
                            subscriber.onNext(bean);
                            subscriber.onCompleted();
                        }
                    }).error(new ErrorCallBack() {
                        @Override
                        public void error(NetworkException e) {
                            subscriber.onError(e);
                        }
                    }).postSync();
                }
            });
        }


        //下载 返回下载后保存地址
        public void download() {
            //要在调用前check
            this.url = checkUrl(this.url);
            this.headers.put(Constant.FILE, Constant.FILE_URL);
            final Call<ResponseBody> call = mService.download(checkHeaders(headers), url, checkParams(this.params));
            putCall(tag, url, call);

            try {
                Response<ResponseBody> response = call.execute();
                if (response.isSuccessful()) {
                    WriteFileUtil.writeFile(response.body(), path, new ProgressCallback() {
                        @Override
                        public void progress(float p) {
                            mProgressCallBack.progress(p);
                        }
                    }, new SuccessCallback<String>() {
                        @Override
                        public void success(String result) {
                            onResult(result);
                        }
                    }, new ErrorCallBack() {
                        @Override
                        public void error(NetworkException e) {
                            onError(e);
                        }
                    });
                } else {
                    onError(NetworkException.network());
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                removeCall();
            }

        }

        // 返回下载后保存地址
        public Observable<String> downloadRx() {
            return Observable.create(new Observable.OnSubscribe<String>() {
                @Override
                public void call(final Subscriber<? super String> subscriber) {
                    success(new SuccessCallback<T>() {
                        @Override
                        public void success(T bean) {
                            subscriber.onNext((String) bean);
                            subscriber.onCompleted();
                        }
                    }).error(
                            new ErrorCallBack() {
                                @Override
                                public void error(NetworkException e) {
                                    subscriber.onError(e);
                                }
                            })
                            .download();

                }
            });
        }


        public void removeCall() {
            if (tag != null) {
                HttpUtil.removeCall(Builder.this.url);
            }
        }

        protected void onResult(String result) {
            try {
                if (token == null) {
                    // 未设置token
                    mSuccessCallBack.success((T) result);
                    return;
                }

                T model = new Gson().fromJson(result, token.getType());
                if (model instanceof Bean) {
                    if (((Bean) model).getCode() != CODE_SUCCESS) {
                        throw NetworkException.from((Bean) model);
                    }
                }
                mSuccessCallBack.success(model);
            } catch (Exception e) {
                onError(NetworkException.from(e));
            }
        }

        protected void onError(NetworkException network) {
            mErrorCallBack.error(network);
        }


    }


}

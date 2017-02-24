package cc.kaipao.dongjia.http.interceptor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by xb on 17/2/21.
 */
public class RedirectInterceptorTest {

    public static final String FILE_SERVER = "http://nicai.cc.dongjia/";
    public static final String APP_SERVER = "http://hello.cc.dongjia/";
    public static final String OTHER_SERVER = "http://world.cc.dongjia/";




    @Test
    public void test_empty() throws Exception {

        Interceptor.Chain chain = Mockito.mock(Interceptor.Chain.class);
        Request request = new Request.Builder().url(APP_SERVER).build();
        when(chain.request()).thenReturn(request);
        RedirectInterceptor interceptor = new RedirectInterceptor(APP_SERVER, new String[1]);

        interceptor.intercept(chain);

        ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);
        verify(chain, times(1)).proceed(captor.capture());

        Assert.assertSame(captor.getValue(), request);
    }

    @Test
    public void test_empty_2() throws Exception {
        Interceptor.Chain chain = Mockito.mock(Interceptor.Chain.class);
        Request request = new Request.Builder().url(APP_SERVER).build();
        when(chain.request()).thenReturn(request);
        RedirectInterceptor interceptor = new RedirectInterceptor(APP_SERVER, new String[]{"__download__"});
        interceptor.intercept(chain);

        ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);
        verify(chain, times(1)).proceed(captor.capture());

        Assert.assertSame(captor.getValue(), request);
    }

    @Test
    public void test_redirect() throws Exception {
        Interceptor.Chain chain = Mockito.mock(Interceptor.Chain.class);
        Request request = new Request.Builder().url(APP_SERVER).header("__download__",FILE_SERVER).build();
        when(chain.request()).thenReturn(request);
        RedirectInterceptor interceptor = new RedirectInterceptor(APP_SERVER, new String[]{"__download__"});

        ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);
        interceptor.intercept(chain);
        verify(chain).proceed(captor.capture());
        assertEquals(FILE_SERVER, captor.getValue().url().toString());
    }

}
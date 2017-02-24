package cc.kaipao.dongjia.http;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.net.Network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import cc.kaipao.dongjia.http.exception.NetworkException;
import cc.kaipao.dongjia.http.interfaces.ErrorCallBack;
import cc.kaipao.dongjia.http.interfaces.SuccessCallback;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

import static org.junit.Assert.*;

/**
 * Created by xb on 17/2/22.
 */
public class HttpUtilTest {

    @Before
    public void setUp() throws Exception {
        Context context = Mockito.mock(Context.class);
        new HttpUtil.SingletonBuilder(context).baseUrl("http://hello.cc.dongjia").build();

    }

    @Test
    public void test_checkUrl() throws Exception {

        Context context = Mockito.mock(Context.class);
        new HttpUtil.SingletonBuilder(context).baseUrl("http://hello.cc.dongjia/").build();

        assertEquals("http://hello.cc.dongjia/v1/test", new HttpUtil.Builder().checkUrl("v1/test"));
    }

    @Test
    public void test_checkUrl_2() throws Exception {

        Context context = Mockito.mock(Context.class);
        new HttpUtil.SingletonBuilder(context).baseUrl("http://hello.cc.dongjia").build();

        assertEquals("http://hello.cc.dongjia/v1/test", new HttpUtil.Builder().checkUrl("v1/test"));
    }

    @Test
    public void test_checkUrl_3() throws Exception {

        Context context = Mockito.mock(Context.class);
        new HttpUtil.SingletonBuilder(context).baseUrl("http://hello.cc.dongjia/").build();

        assertEquals("http://hello.cc.dongjia/v1/test", new HttpUtil.Builder().checkUrl("/v1/test"));
    }

    @Test
    public void test_checkUrl_4() throws Exception {

        Context context = Mockito.mock(Context.class);
        new HttpUtil.SingletonBuilder(context).baseUrl("http://hello.cc.dongjia").build();

        assertEquals("http://www.baidu.com", new HttpUtil.Builder().checkUrl("http://www.baidu.com"));
    }


    @Test
    public void test_go() throws Exception {


    }


    @Test
    public void test_transform() throws Exception {

        User user = new User(0, "2");
        Bean<User> bean = new Bean<>(0, "hello", user);

        new HttpUtil.Builder<Bean<User>>()
                .token(new TypeToken<Bean<User>>(){})
                .success(new SuccessCallback<Bean<User>>() {
                    @Override
                    public void success(Bean<User> bean) {
                        assertEquals(0,bean.getRes().age);
                        assertEquals("2",bean.getRes().name);
                    }
                })
                .error(new ErrorCallBack() {
                    @Override
                    public void error(NetworkException e) {
                        Assert.fail();
                    }
                })
                .onResult(new Gson().toJson(bean));

    }

    @Test
    public void test_transform2() throws Exception {

        final User user = new User(0, "2");
        Bean<User> bean = new Bean<>(0, "hello", user);


    }

    @Test
    public void test_mockapi() throws Exception {

        mockApi().success(new SuccessCallback<Bean<User>>() {
            @Override
            public void success(Bean<User> bean) {

            }
        });


        mockApi().postRx().subscribe(new Subscriber<Bean<User>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Bean<User> userBean) {

            }
        });



    }

    public HttpUtil.Builder<Bean<User>> mockApi() {
        return new HttpUtil.Builder<Bean<User>>("v1/test")
                .params("p1", "v1")
                .params("p2", "v2")
                .token(new TypeToken<Bean<User>>(){});
    }




    @Test
    public void test_generic_method() throws Exception {




    }



    class User {
        int age;
        String name;

        public User(int age, String name) {
            this.age = age;
            this.name = name;
        }
    }

}
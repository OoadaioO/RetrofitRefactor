package cc.kaipao.dongjia.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by xb on 17/2/21.
 */

public class Bean<T> {

    int code;
    String msg;
    T res;

    public Bean(int code, String msg, T res) {
        this.code = code;
        this.msg = msg;
        this.res = res;
    }

    public static <T> T from(String json) {
        return new Gson().fromJson(json, new TypeToken<T>() {
        }.getType());
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        if (msg == null) {
            return "";
        }
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getRes() {
        return res;
    }

    public void setRes(T res) {
        this.res = res;
    }
}

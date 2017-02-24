package cc.kaipao.dongjia.http.exception;

import cc.kaipao.dongjia.http.Bean;

/**
 * Created by xb on 17/2/22.
 */

public class NetworkException extends RuntimeException {

    static final int NONE = 0;
    int code = NONE;

    public NetworkException(int code, String message) {
        super(message);
        this.code = code;
    }

    public NetworkException(Bean bean) {
        this(bean.getCode(), bean.getMsg());
    }

    public NetworkException(String message) {
        super(message);
    }

    public NetworkException(Throwable cause) {
        super(cause);
    }

    public NetworkException(String message, Throwable cause) {
        super(message, cause);
    }

    public static NetworkException network() {
        return new NetworkException("网络异常,请检查您的网络");
    }

    public static NetworkException from(Exception e) {
        if (e instanceof NetworkException) {
            return (NetworkException) e;
        }
        return new NetworkException(e);
    }

    public static NetworkException from(Bean bean) {
        return new NetworkException(bean.getCode(), bean.getMsg());
    }
}

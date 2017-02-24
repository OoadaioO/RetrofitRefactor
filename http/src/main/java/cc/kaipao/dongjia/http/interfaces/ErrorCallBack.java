package cc.kaipao.dongjia.http.interfaces;

import cc.kaipao.dongjia.http.exception.NetworkException;

/**
 * Created by xb on 17/2/21.
 */

public interface ErrorCallBack {
    void error(NetworkException e);
}

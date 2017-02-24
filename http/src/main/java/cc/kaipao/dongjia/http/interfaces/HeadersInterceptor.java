package cc.kaipao.dongjia.http.interfaces;

import java.util.Map;

/**
 * Created by xb on 17/2/21.
 */

public interface HeadersInterceptor {
    Map checkHeaders(Map headers);
}

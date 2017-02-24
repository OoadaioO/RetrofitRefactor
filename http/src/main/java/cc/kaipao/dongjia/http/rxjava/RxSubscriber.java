package cc.kaipao.dongjia.http.rxjava;

import cc.kaipao.dongjia.http.exception.NetworkException;
import rx.Subscriber;

/**
 * Created by xb on 17/2/24.
 */

public abstract class RxSubscriber<T> extends Subscriber<T> {

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        _onError(e);
    }

    @Override
    public void onNext(T t) {
        _onNext(t);
    }


    public abstract void _onNext(T t);

    public abstract void _onError(Throwable e);

}

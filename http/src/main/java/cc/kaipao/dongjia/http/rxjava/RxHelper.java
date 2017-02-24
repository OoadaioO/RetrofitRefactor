package cc.kaipao.dongjia.http.rxjava;

import rx.Observable;

/**
 * Created by xb on 17/2/24.
 */

public class RxHelper {

    // 切换主线程和io线程
    public static <T>Observable.Transformer<T,T> http_main(){
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable.subscribeOn(SchedulersAdapter.http())
                        .observeOn(SchedulersAdapter.mainThread());
            }
        };
    }


}

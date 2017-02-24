package cc.kaipao.dongjia.http.rxjava;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

/**
 * Created by xb on 16/11/9.
 * 内部scheduler代理
 */

public class SchedulersAdapter {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "SchedulersAdapter #" + mCount.getAndIncrement());
        }
    };

    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(128);

    public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);

    // 带宽限制,传一个文件和传多个文件实际速度其实是差不多的,所以文件上传使用单线程
    public static final Executor FILE_POOL_EXECUTOR = Executors.newFixedThreadPool(1);

    static SchedulersAdapter instance = new SchedulersAdapter();

    Scheduler schedulerHttp;
    Scheduler schedulerHttpOfFile;

    private SchedulersAdapter() {
        schedulerHttp = rx.schedulers.Schedulers.from(THREAD_POOL_EXECUTOR);
        schedulerHttpOfFile = Schedulers.from(FILE_POOL_EXECUTOR);
    }

    private static SchedulersAdapter getInstance() {
        return instance;
    }


    public static Scheduler io() {
        return rx.schedulers.Schedulers.io();
    }

    public static Scheduler http() {
        return getInstance().schedulerHttp;
    }

    public static Scheduler httpOfFile() {
        return getInstance().schedulerHttpOfFile;
    }


    public static Scheduler computation() {
        return rx.schedulers.Schedulers.computation();
    }

    public static Scheduler immediate() {
        return rx.schedulers.Schedulers.immediate();
    }

    public static Scheduler newThread() {
        return rx.schedulers.Schedulers.newThread();
    }

    public static void reset() {
        rx.schedulers.Schedulers.reset();
    }

    public static void shutdown() {
        rx.schedulers.Schedulers.shutdown();
    }

    public static void trampoline() {
        SchedulersAdapter.trampoline();
    }

    public static TestScheduler test() {
        return rx.schedulers.Schedulers.test();
    }

    public static Scheduler mainThread() {
        return AndroidSchedulers.mainThread();
    }


}

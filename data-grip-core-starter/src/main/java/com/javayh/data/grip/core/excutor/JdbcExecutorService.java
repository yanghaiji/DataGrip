package com.javayh.data.grip.core.excutor;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 线程池
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 */
public class JdbcExecutorService {

    private static final int CORE = Runtime.getRuntime().availableProcessors() * 2 + 1;

    private volatile static ThreadPoolExecutor singleton;

    private JdbcExecutorService() {
    }

    /**
     * <p>
     * 创建线程池
     * </p>
     *
     * @return java.util.concurrent.ThreadPoolExecutor
     */
    public static ThreadPoolExecutor executor() {
        if (singleton == null) {
            synchronized (JdbcExecutorService.class) {
                if (singleton == null) {
                    singleton = new ThreadPoolExecutor(CORE, CORE, 0L, TimeUnit.MILLISECONDS,
                            new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(),
                            new ThreadPoolExecutor.AbortPolicy());
                }
            }
        }
        return singleton;
    }

}

package com.app.thread;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * 处理注册登录等账号相关线程池
 * @author doter
 */
public abstract class ThreadPool implements RejectedExecutionHandler {
    /** 线程池 */
    private ThreadPoolExecutor      mThreadPoolExecutor;
    /** 线程队列 */
    private BlockingQueue<Runnable> workQueue;
    /** 线程最小数 */
    private static final int        CORE_POOL_SIZE    = 2;
    /** 线程最大数 */
    private static final int        MAXIMUM_POOL_SIZE = 120;
    /** 线程有效时间,单位为秒 */
    private static final long       KEEP_ALIVE_TIME   = 60;
    /** 等待队列数*/
    private static final int        QUEUR_SIZE        = 150;

    /**
     * 构造函数，初始化线程池及队列
     */
    public ThreadPool() {
        workQueue = new LinkedBlockingQueue<Runnable>(QUEUR_SIZE);
        mThreadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue, this);
    }

    /**
     * 构造函数，初始化线程池及队列
     * @param minPoolSize   最小线程数
     * @param maxPoolSize   最大线程数
     * @param queurSize     等待队列大小
     */
    public ThreadPool(int minPoolSize, int maxPoolSize, int queurSize) {
        workQueue = new LinkedBlockingQueue<Runnable>(queurSize);
        mThreadPoolExecutor = new ThreadPoolExecutor(minPoolSize, maxPoolSize, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue, this);
    }

    /**
     * 关闭线程池
     */
    public void showdown() {
        mThreadPoolExecutor.shutdown();
    }

    /**
     * 执行线程
     * @param runnable  执行任务
     */
    public void execute(Runnable runnable) {
        mThreadPoolExecutor.execute(runnable);
    }

    /**
     * 获取线程池容量
     * @return  线程池容量
     */
    public int getMaximumPoolSize() {
        return mThreadPoolExecutor.getMaximumPoolSize();
    }

    /**
     * 获取线程池当前已用线程数
     * @return  线程池当前已用线程数
     */
    public int getActiveCount() {
        return mThreadPoolExecutor.getActiveCount();
    }

    /**
     * 线程池已满,拒绝线程
     * @param   runnable            任务信息
     * @param   threadPoolExecutor  异常信息
     */
    public abstract void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor);
}

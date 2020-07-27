package com.cyy.myapplication;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * @author chenyy
 * @date 2020/7/15
 */

public class ConcurrencyTest {

    // 请求总数
    public static int clientTotal = 5000;

    // 同时并发执行的线程数
    public static int threadTotal = 200;

    // 全局变量
    public static int count = 0;

    public static void test() throws InterruptedException {

        // 创建线程池
        ExecutorService executorService = Executors.newCachedThreadPool();

        // 信号量
        final Semaphore semaphore = new Semaphore(threadTotal);

        // 计数器闭锁
        final CountDownLatch countDownLatch = new CountDownLatch(clientTotal);

        for (int i=0;i<clientTotal;i++){
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        semaphore.acquire();
                        add();
                        semaphore.release();
                    } catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                    // 每个线程执行时计数器都减1
                    countDownLatch.countDown();
                }
            });
        }

        System.out.println("countDownLatch is awaiting!");
        countDownLatch.await();

        executorService.shutdown();
        System.out.println("count="+count);
    }

    public static synchronized void add(){
        count ++;
    }
}
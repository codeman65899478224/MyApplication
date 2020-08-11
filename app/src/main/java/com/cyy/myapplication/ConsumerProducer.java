package com.cyy.myapplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author chenyy
 * @date 2020/8/10
 */

public class ConsumerProducer {
    public static final Object LOCK = new Object();
    public static int count = 0;
    public static final int FULL = 10;

    public static void process(){
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 5; i++) {
            executorService.execute(new Consumer());
            executorService.execute(new Producer());
        }
    }

    public static class Consumer implements Runnable{

        @Override
        public void run() {
            synchronized (LOCK){
                while (count == 0){
                    try {
                        LOCK.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                count--;
                System.out.println(Thread.currentThread().getName() + "消费者消费，目前总共有" + count);
                LOCK.notifyAll();
            }
        }
    }

    public static class Producer implements Runnable{

        @Override
        public void run() {
            synchronized (LOCK){
                while (count == FULL){
                    try {
                        LOCK.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                count++;
                System.out.println(Thread.currentThread().getName() + "生产者生产，目前总共有" + count);
                LOCK.notifyAll();
            }
        }
    }
}

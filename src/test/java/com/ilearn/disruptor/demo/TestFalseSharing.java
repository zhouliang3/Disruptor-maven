package com.ilearn.disruptor.demo;

import sun.misc.Contended;

/**
 * 功能说明:
 *
 * @author zhouliang
 * @Date 2016-11-29
 * <p/>
 * <p/>
 * 版本号 | 作者 | 修改时间 | 修改内容
 */
public class TestFalseSharing implements Runnable {
    public final static long ITERATIONS = 500L * 1000L * 100L;
    private int arrayIndex = 0;
    private static ValuePadding[] longs;

    public TestFalseSharing(final int arrayIndex) {
        this.arrayIndex = arrayIndex;
    }

    public static void main(final String[] args) throws Exception {
        for (int i = 1; i < 10; i++) {
            System.gc();
            final long start = System.currentTimeMillis();
            runTest(i);
            System.out.println("Thread num " + i + " duration = " + (System.currentTimeMillis() - start));
        }

    }

    private static void runTest(int NUM_THREADS) throws InterruptedException {
        Thread[] threads = new Thread[NUM_THREADS];
        longs = new ValuePadding[NUM_THREADS];
        for (int i = 0; i < longs.length; i++) {
            longs[i] = new ValuePadding();
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new TestFalseSharing(i));
        }
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }
    }

    public void run() {
        long i = ITERATIONS + 1;
        while (0 != --i) {
            longs[arrayIndex].value = 0L;
        }
    }

    @Contended
    public final static class ValuePadding {
        //        protected long p1, p2, p3, p4, p5, p6, p7;
        protected volatile long value = 0L;
//        protected long p9, p10, p11, p12, p13, p14;
//        protected long p15;
    }

    public final static class ValueNoPadding {            // protected long p1, p2, p3, p4, p5, p6, p7;
        protected volatile long value = 0L;            // protected long p9, p10, p11, p12, p13, p14, p15;
    }
}
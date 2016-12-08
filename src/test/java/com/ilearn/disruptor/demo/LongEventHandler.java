package com.ilearn.disruptor.demo;

import com.lmax.disruptor.EventHandler;

/**
 * Created by lenovo on 2015/12/10 0010.${}
 */
public class LongEventHandler implements EventHandler<LongEvent> {

    @Override
    public void onEvent(LongEvent event, long sequence, boolean endOfBatch) throws Exception {
        System.out.println("Event:" + event);
    }
}

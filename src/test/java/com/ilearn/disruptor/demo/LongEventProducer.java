package com.ilearn.disruptor.demo;

import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

/**
 * Created by lenovo on 2015/12/10 0010.${}
 */
public class LongEventProducer {
    private final RingBuffer<LongEvent> ringBuffer;
    public LongEventProducer(RingBuffer<LongEvent> ringBuffer){
        this.ringBuffer = ringBuffer;
    }

    public void onData(ByteBuffer bb){
        long sequence = this.ringBuffer.next();
        try{
            LongEvent longEvent = ringBuffer.get(sequence);
            longEvent.set(bb.getLong(0));
        }finally {
            ringBuffer.publish(sequence);
        }
    }
}

package com.ilearn.disruptor.demo;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

/**
 * Created by lenovo on 2015/12/10 0010.${}
 */
public class LongEventProducerWithTranslator {
    private final RingBuffer<LongEvent> ringBuffer;
    public LongEventProducerWithTranslator(RingBuffer<LongEvent> ringBuffer){
        this.ringBuffer = ringBuffer;
    }

    private static final EventTranslatorOneArg<LongEvent,ByteBuffer> TRANSLATOR_ONE_ARG = new EventTranslatorOneArg<LongEvent, ByteBuffer>() {
        @Override
        public void translateTo(LongEvent event, long sequence, ByteBuffer bb) {
            event.set(bb.getLong());
        }
    };

    public void onData (ByteBuffer bb){
        ringBuffer.publishEvent(TRANSLATOR_ONE_ARG,bb);
    }

}

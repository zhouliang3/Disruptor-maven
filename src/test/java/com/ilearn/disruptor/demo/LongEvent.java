package com.ilearn.disruptor.demo;

import com.lmax.disruptor.EventFactory;

/**
 * Created by lenovo on 2015/12/10 0010.${}
 */
public class LongEvent {
    private long value;

    public void set(long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "LongEvent{" +
                "value=" + value +
                '}';
    }
    public static class LongEventFactory implements EventFactory<LongEvent> {

        @Override
        public LongEvent newInstance() {
            return new LongEvent();
        }
    }
}

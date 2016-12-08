package com.lmax.disruptor;

/**
 * inote 实现此接口类，可以理解为，实现一个有序的存储结构，也就是RingBuffer的一个特性。一个Producer，在生产Event时，先获取下一位置的Sequence，之后填充Event，填充好后再publish，这之后，这个Event就可以被消费处理了
 */
public interface Sequenced
{
    /**
     * The capacity of the data structure to hold entries.
     * inote 获取ringBuffer的大小
     * @return the size of the RingBuffer.
     */
    int getBufferSize();

    /**
     * inote 判断空间是否足够
     * Has the buffer got capacity to allocate another sequence.  This is a concurrent
     * method so the response should only be taken as an indication of available capacity.
     *
     * @param requiredCapacity in the buffer
     * @return true if the buffer has the capacity to allocate the next sequence otherwise false.
     */
    boolean hasAvailableCapacity(final int requiredCapacity);

    /**
     * inote 获取ringBuffer的剩余空间
     * Get the remaining capacity for this sequencer.
     *
     * @return The number of slots remaining.
     */
    long remainingCapacity();

    /**
     * inote 申请下一个或者n个sequence(value)作为生产event的位置
     * Claim the next event in sequence for publishing.
     *
     * @return the claimed sequence value
     */
    long next();

    /**
     * Claim the next n events in sequence for publishing.  This is for batch event producing.  Using batch producing
     * requires a little care and some math.
     * <pre>
     * int n = 10;
     * long hi = sequencer.next(n);
     * long lo = hi - (n - 1);
     * for (long sequence = lo; sequence &lt;= hi; sequence++) {
     *     // Do work.
     * }
     * sequencer.publish(lo, hi);
     * </pre>
     *
     * @param n the number of sequences to claim
     * @return the highest claimed sequence value
     */
    long next(int n);

    /**
     * inote 尝试申请下一个或者n个sequence(value)作为生产event的位置，容量不足会抛出InsufficientCapacityException
     * Attempt to claim the next event in sequence for publishing.  Will return the
     * number of the slot if there is at least <code>requiredCapacity</code> slots
     * available.
     *
     * @return the claimed sequence value
     * @throws InsufficientCapacityException
     */
    long tryNext() throws InsufficientCapacityException;

    /**
     * Attempt to claim the next n events in sequence for publishing.  Will return the
     * highest numbered slot if there is at least <code>requiredCapacity</code> slots
     * available.  Have a look at {@link Sequencer#next()} for a description on how to
     * use this method.
     *
     * @param n the number of sequences to claim
     * @return the claimed sequence value
     * @throws InsufficientCapacityException
     */
    long tryNext(int n) throws InsufficientCapacityException;

    /**
     * inote 发布Event
     * Publishes a sequence. Call when the event has been filled.
     *
     * @param sequence
     */
    void publish(long sequence);

    /**
     * Batch publish sequences.  Called when all of the events have been filled.
     *
     * @param lo first sequence number to publish
     * @param hi last sequence number to publish
     */
    void publish(long lo, long hi);
}
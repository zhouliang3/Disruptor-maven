/*
 * Copyright 2012 LMAX Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lmax.disruptor;

/**
 * inote 扩展了Cursored和Sequenced接口。在前两者的基础上，增加了消费与生产相关的方法。其中一个比较重要的设计是关于**GatingSequence的设计：
 * inote RingBuffer的头由一个名字为cursor的Sequence对象维护，用来协调生产者向RingBuffer中填充数据。表示队列尾的Sequence并没有在RingBuffer中，而是由消费者维护。这样的话，队列尾的维护就是无锁的。但是，在生产者方确定RingBuffer是否已满就需要跟踪更多信息。为此，GatingSequence用来跟踪相关Sequence
 * Coordinates claiming sequences for access to a data structure while tracking dependent {@link Sequence}s
 */
public interface Sequencer extends Cursored, Sequenced
{
    /**
     * inote -1 为sequence的起始值
     * Set to -1 as sequence starting point
     */
    long INITIAL_CURSOR_VALUE = -1L;

    /**
     * inote 申请一个特殊的Sequence，只有设定特殊起始值的ringBuffer时才会使用（一般是多个生产者时才会使用）
     * Claim a specific sequence.  Only used if initialising the ring buffer to
     * a specific value.
     *
     * @param sequence The sequence to initialise too.
     */
    void claim(long sequence);

    /**
     * inote 非阻塞，验证一个sequence是否已经被published并且可以消费
     * Confirms if a sequence is published and the event is available for use; non-blocking.
     *
     * @param sequence of the buffer to check
     * @return true if the sequence is available for use, false if not
     */
    boolean isAvailable(long sequence);

    /**
     * inote 将这些sequence加入到需要跟踪处理的gatingSequences中
     * Add the specified gating sequences to this instance of the Disruptor.  They will
     * safely and atomically added to the list of gating sequences.
     *
     * @param gatingSequences The sequences to add.
     */
    void addGatingSequences(Sequence... gatingSequences);

    /**
     * inote 移除某个sequence
     * Remove the specified sequence from this sequencer.
     *
     * @param sequence to be removed.
     * @return <tt>true</tt> if this sequence was found, <tt>false</tt> otherwise.
     */
    boolean removeGatingSequence(Sequence sequence);

    /**
     * inote 给定一串需要跟踪的sequence，创建SequenceBarrier。SequenceBarrier是用来给多消费者确定消费位置是否可以消费用的
     * Create a new SequenceBarrier to be used by an EventProcessor to track which messages
     * are available to be read from the ring buffer given a list of sequences to track.
     *
     * @param sequencesToTrack
     * @return A sequence barrier that will track the specified sequences.
     * @see SequenceBarrier
     */
    SequenceBarrier newBarrier(Sequence... sequencesToTrack);

    /**
     * inote 获取这个ringBuffer的gatingSequences中最小的一个sequence
     * Get the minimum sequence value from all of the gating sequences
     * added to this ringBuffer.
     *
     * @return The minimum gating sequence or the cursor sequence if
     * no sequences have been added.
     */
    long getMinimumSequence();

    /**
     * inote 获取最高可以读取的Sequence
     * Get the highest sequence number that can be safely read from the ring buffer.  Depending
     * on the implementation of the Sequencer this call may need to scan a number of values
     * in the Sequencer.  The scan will range from nextSequence to availableSequence.  If
     * there are no available values <code>&gt;= nextSequence</code> the return value will be
     * <code>nextSequence - 1</code>.  To work correctly a consumer should pass a value that
     * it 1 higher than the last sequence that was successfully processed.
     *
     * @param nextSequence      The sequence to start scanning from.
     * @param availableSequence The sequence to scan to.
     * @return The highest value that can be safely read, will be at least <code>nextSequence - 1</code>.
     */
    long getHighestPublishedSequence(long nextSequence, long availableSequence);

    <T> EventPoller<T> newPoller(DataProvider<T> provider, Sequence... gatingSequences);
}
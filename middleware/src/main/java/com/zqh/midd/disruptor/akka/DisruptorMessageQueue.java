/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Igor Konev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.zqh.midd.disruptor.akka;

import akka.actor.ActorRef;
import akka.dispatch.Envelope;
import akka.dispatch.MessageQueue;
import com.lmax.disruptor.AlertException;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.WaitStrategy;

final class DisruptorMessageQueue implements MessageQueue, DisruptorMessageQueueSemantics {

    private static final EventFactory<ValueEvent> EVENT_FACTORY = new EventFactory<ValueEvent>() {
        @Override
        public ValueEvent newInstance() {
            return new ValueEvent();
        }
    };

    private final Sequence sequence = new Sequence();
    private final RingBuffer<ValueEvent> ringBuffer;
    private final SequenceBarrier sequenceBarrier;
    private final int mask;
    private final Envelope[] buffer;
    private int head;   //消费者消费的位置
    private int tail;   //生产者生产的位置

    DisruptorMessageQueue(int bufferSize, WaitStrategy waitStrategy) {
        ringBuffer = RingBuffer.createMultiProducer(EVENT_FACTORY, bufferSize, waitStrategy);
        ringBuffer.addGatingSequences(sequence);
        sequenceBarrier = ringBuffer.newBarrier();
        mask = bufferSize - 1;
        buffer = new Envelope[bufferSize];
    }

    //入队, 往RingBuffer中发布/生产序列号
    @Override
    public void enqueue(ActorRef receiver, Envelope handle) {
        //取下一个序列号. 因为当前的已经生产过了! 发布消息时, 首先要申请到RingBuffer中下一个可用的序列号, 如果没有, 则无法生产消息
        long nextSequence = ringBuffer.next();
        ringBuffer.get(nextSequence).handle = handle;  // 填充消息: 包括消息内容message和发送者ActorRef
        ringBuffer.publish(nextSequence);
    }

    //出队, 消费RingBuffer中的序列号
    @Override
    public Envelope dequeue() {
        fill();
        int h = head & mask;
        head++;
        Envelope handle = buffer[h];
        buffer[h] = null;
        return handle;
    }

    @Override
    public int numberOfMessages() {
        return 0;
    }

    //是否有消息需要处理. 如果head=tail, 则没有消息需要处理
    //发布消息时, tail++, 消费消息时[dequeue], head++
    @Override
    public boolean hasMessages() {
        return head != tail || sequence.get() < sequenceBarrier.getCursor();
    }

    @Override
    public void cleanUp(ActorRef owner, MessageQueue deadLetters) {
        while (hasMessages()) {
            deadLetters.enqueue(owner, dequeue());
        }
    }

    /**
     * 消费消息时, 首先进行填充.
     */
    private void fill() {
        //如果head=tail,才需要填充. 说明消费者已经把生产者的数据都消费完了!
        //如果不相等, 说明还有消息没有消费完, 则不需要填充. 在消费消息时, 从head位置继续消费
        if (head != tail) {
            return;
        }

        //head=tail
        long nextSequence = sequence.get() + 1L;
        boolean interrupted = false;
        long availableSequence;
        try {
            while (true) {
                try {
                    availableSequence = sequenceBarrier.waitFor(nextSequence);
                    if (nextSequence <= availableSequence) {
                        break;
                    }
                    Thread.yield();
                } catch (AlertException ignored) {
                    interrupted = true;
                } catch (InterruptedException ignored) {
                    interrupted = true;
                } catch (TimeoutException ignored) {
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }

        int t = tail;
        do {
            buffer[t & mask] = ringBuffer.get(nextSequence).handle;
            t++;
            nextSequence++;
        } while (nextSequence <= availableSequence);

        tail = t;
        sequence.set(availableSequence);
    }

    private static final class ValueEvent {

        Envelope handle;
    }
}

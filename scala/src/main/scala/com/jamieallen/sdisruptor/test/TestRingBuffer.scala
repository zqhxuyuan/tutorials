package com.jamieallen.sdisruptor.test

import com.jamieallen.sdisruptor._

/**
 * Created by zqhxuyuan on 15-3-2.
 */
object TestRingBuffer {

  def main(args: Array[String]) {
    val ringBuffer : RingBuffer[ValueEntry] =
      new RingBuffer[ValueEntry](ValueEntry(1L), 20,
        null.asInstanceOf[String], null.asInstanceOf[String]);

    val consumers : Array[Consumer] = Array(
      new NoOpConsumer(ringBuffer)
    )
    val consumerBarrier : ConsumerBarrier[ValueEntry] = ringBuffer.createConsumerBarrier(consumers)
    val batchHandler : BatchHandler[ValueEntry] = new MyHandler
    val batchConsumer : BatchConsumer[ValueEntry] = new BatchConsumer[ValueEntry](consumerBarrier, batchHandler);



    val entry : ValueEntry = ringBuffer.nextEntry()
    entry.setValue(123)
    ringBuffer.commit(entry)
  }
}

class ValueEntry(value : Long) extends AbstractEntry{
  override var _sequence: Long = 1L

  def setValue(value : Long): Unit = {
    _sequence = value
  }

  val entry : EntryFactory[ValueEntry] = new EntryFactory[ValueEntry]() {
    override def create(): ValueEntry = new ValueEntry(value)
  }
}

object ValueEntry{
  def apply(value : Long) = {
    val v : ValueEntry = new ValueEntry(value)
    v.entry
  }
}

class MyHandler extends BatchHandler[ValueEntry]{
  override def onAvailable(entry: ValueEntry): Unit = {
    println("onAvailable")
  }

  override def onEndOfBatch(): Unit = {
    println("onEndOfBatch")
  }
}

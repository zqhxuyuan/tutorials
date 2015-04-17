/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.coderplay.util.concurrent.queue;

/**
 * @author Min Zhou (coderplay@gmail.com)
 */
public class MultiThreadedLowContentionWaitStrategy extends
        AbstractMutlithreadedWaitStrategy {

  @Override
  public void publish(long sequence, Sequence lowerCursor) {
    final long expectedSequence = sequence - 1L;
    while (expectedSequence != lowerCursor.get()) {
      if (Thread.interrupted()) 
        return;
      // busy spin
    }
    lowerCursor.set(sequence);
  }

  @Override
  public void publishInterruptibly(long sequence, Sequence lowerCursor)
      throws InterruptedException {
    final long expectedSequence = sequence - 1L;
    while (expectedSequence != lowerCursor.get()) {
      if (Thread.interrupted())
        throw new InterruptedException();
      // busy spin
    }
    lowerCursor.set(sequence);
  }

}
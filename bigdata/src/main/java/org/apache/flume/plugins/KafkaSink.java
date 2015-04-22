/*
 *  Copyright (c) 2013.09.06 BeyondJ2EE.
 *  * All right reserved.
 *  * http://beyondj2ee.github.com
 *  * This software is the confidential and proprietary information of BeyondJ2EE
 *  * , Inc. You shall not disclose such Confidential Information and
 *  * shall use it only in accordance with the terms of the license agreement
 *  * you entered into with BeyondJ2EE.
 *  *
 *  * Revision History
 *  * Author              Date                  Description
 *  * ===============    ================       ======================================
 *  *  beyondj2ee
 *
 */

package org.apache.flume.plugins;

/**
 * KAFKA Flume Sink (Kafka 0.8 Beta, Flume 1.4).
 * User: beyondj2ee
 * Date: 13. 9. 4
 * Time: PM 4:32
 *
 * 注释 by zqh @ 2014-05-14
 * flume-conf.properties的sink为KafkaSink. 说明数据从source到sink.
 * sink为消费端. 由于是Kafka, 所以要启动一个Producer, 将数据写到kafka队列中.
 *
 * We mock tail data as A Flume Source, We want tail data flow to Kafka.
 * KafkaSink means Sink msg to Kafka, means that KafkaSink as a Flume Sink.
 */
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.apache.commons.lang.StringUtils;
import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.EventHelper;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class KafkaSink extends AbstractSink implements Configurable {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaSink.class);

    // flume配置以及上下文
    private Properties parameters;
    private Context context;

    // 生产者
    private Producer<String, String> producer;

    @Override
    public void configure(Context context) {
        this.context = context;
        ImmutableMap<String, String> props = context.getParameters();

        // base: producer.sinks.r
        //假设启动Flume-Agent时指定flume-conf.properties为配置文件参数:
        //bin/flume-ng agent --conf conf --conf-file conf/flume-conf.properties --name producer
        //因为指定了--name, 所以对应的配置参数为producer开头的producer.sinks.r
        parameters = new Properties();
        for (String key : props.keySet()) {
            String value = props.get(key);
            this.parameters.put(key, value);
        }
    }

    // 启动KafkaSink, 创建一个生产者
    @Override
    public synchronized void start() {
        super.start();
        //将配置文件flume-conf.properties的producer.sinks.r作为Kafka的ProducerConfig的配置参数
        ProducerConfig config = new ProducerConfig(this.parameters);
        //创建一个Kafka的消息生产者
        this.producer = new Producer<String, String>(config);
    }

    /**
     * Flume的Sink在收到Source经过Channel传送过来的数据, 会写到Sink中
     * 因为Sink是Kafka, 所以Source的数据会写到Kafka消息队列中.
     * 由于Kafka写数据相当于生产消息, 所以在启动Sink时要创建Producer生产者,
     * 在接收到Source传来的数据后, 调用生产者发布消息的方法, 将数据发布到消息队列中
     */
    @Override
    public Status process() throws EventDeliveryException {
        Status status = null;

        // Start transaction 开启事务
        Channel ch = getChannel();  //实际上是获得producer.sinks.r.channel
        Transaction txn = ch.getTransaction();
        txn.begin();
        try {
            // This try clause includes whatever Channel operations you want to do
            //Source的数据会经过Channel,到达Sink. 所以获取数据时是从Channel中获取!
            //Channel可以配置为文件或者内存,表示Source的数据临时保存的位置是文件和内存
            Event event = ch.take();

            String partitionKey = (String) parameters.get(KafkaFlumeConstans.PARTITION_KEY_NAME);
            String encoding = StringUtils.defaultIfEmpty(
                    (String) this.parameters.get(KafkaFlumeConstans.ENCODING_KEY_NAME),
                    KafkaFlumeConstans.DEFAULT_ENCODING);
            // flume-conf.properties中producer.sinks.r.custom.topic.name的配置项
            String topic = Preconditions.checkNotNull(
                    (String) this.parameters.get(KafkaFlumeConstans.CUSTOM_TOPIC_KEY_NAME),
                    "custom.topic.name is required");

            String eventData = new String(event.getBody(), encoding);

            KeyedMessage<String, String> data;

            // if partition key does'nt exist
            //构造Kafka消息队列需要的KeyedMessage, 指定消息的主题和消息正文
            if (StringUtils.isEmpty(partitionKey)) {
                data = new KeyedMessage<String, String>(topic, eventData);
            } else {
                data = new KeyedMessage<String, String>(topic, partitionKey, eventData);
            }

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Send Message to Kafka : [" + eventData + "] -- [" + EventHelper.dumpEvent(event) + "]");
            }
            // 生产者发送数据, 即往topic中写入数据
            producer.send(data);

            // 提交事务
            txn.commit();
            status = Status.READY;
        } catch (Throwable t) {
            txn.rollback();
            status = Status.BACKOFF;

            // re-throw all Errors
            if (t instanceof Error) {
                throw (Error) t;
            }
        } finally {
            txn.close();
        }
        return status;
    }

    @Override
    public void stop() {
        producer.close();
    }
}

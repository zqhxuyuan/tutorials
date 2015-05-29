package com.zdatainc.rts.spark;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

import com.zdatainc.rts.model.Properties;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.*;
import org.apache.spark.streaming.*;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka.*;
import scala.Tuple2;
import scala.Tuple3;
import scala.Tuple4;
import scala.Tuple5;

import java.util.*;

public class SentimentAnalysis
{
    private final Logger LOG = Logger.getLogger(this.getClass());
    private static final String KAFKA_TOPIC =
        Properties.getString("rts.spark.kafka_topic");
    private static final int KAFKA_PARALLELIZATION =
        Properties.getInt("rts.spark.kafka_parallelization");

    public static void main(String[] args)
    {
        BasicConfigurator.configure();
        SparkConf conf = new SparkConf()
                         .setAppName("Twitter Sentiment Analysis");

        if (args.length > 0)
            conf.setMaster(args[0]);
        else
            conf.setMaster("local[2]");

        JavaStreamingContext ssc = new JavaStreamingContext(
            conf,
            new Duration(2000));

        Map<String, Integer> topicMap = new HashMap<String, Integer>();
        topicMap.put(KAFKA_TOPIC, KAFKA_PARALLELIZATION);

        JavaPairReceiverInputDStream<String, String> messages =
            KafkaUtils.createStream(
                ssc,
                Properties.getString("rts.spark.zkhosts"),
                "twitter.sentimentanalysis.kafka",
                topicMap);

        JavaDStream<String> json = messages.map(
            new Function<Tuple2<String, String>, String>() {
                private static final long serialVersionUID = 42l;
                @Override
                public String call(Tuple2<String, String> message) {
                    return message._2();
                }
            }
        );

        JavaPairDStream<Long, String> tweets = json.mapToPair(
            new TwitterFilterFunction());

        JavaPairDStream<Long, String> filtered = tweets.filter(
            new Function<Tuple2<Long, String>, Boolean>() {
                private static final long serialVersionUID = 42l;
                @Override
                public Boolean call(Tuple2<Long, String> tweet) {
                    return tweet != null;
                }
            }
        );

        JavaDStream<Tuple2<Long, String>> tweetsFiltered = filtered.map(
            new TextFilterFunction());

        tweetsFiltered = tweetsFiltered.map(
            new StemmingFunction());

        JavaPairDStream<Tuple2<Long, String>, Float> positiveTweets =
            tweetsFiltered.mapToPair(new PositiveScoreFunction());

        JavaPairDStream<Tuple2<Long, String>, Float> negativeTweets =
            tweetsFiltered.mapToPair(new NegativeScoreFunction());

        JavaPairDStream<Tuple2<Long, String>, Tuple2<Float, Float>> joined =
            positiveTweets.join(negativeTweets);

        JavaDStream<Tuple4<Long, String, Float, Float>> scoredTweets =
            joined.map(new Function<Tuple2<Tuple2<Long, String>,
                                           Tuple2<Float, Float>>,
                                    Tuple4<Long, String, Float, Float>>() {
            private static final long serialVersionUID = 42l;
            @Override
            public Tuple4<Long, String, Float, Float> call(
                Tuple2<Tuple2<Long, String>, Tuple2<Float, Float>> tweet)
            {
                return new Tuple4<Long, String, Float, Float>(
                    tweet._1()._1(),
                    tweet._1()._2(),
                    tweet._2()._1(),
                    tweet._2()._2());
            }
        });

        JavaDStream<Tuple5<Long, String, Float, Float, String>> result =
            scoredTweets.map(new ScoreTweetsFunction());

        result.foreachRDD(new FileWriter());
        result.foreachRDD(new HTTPNotifierFunction());

        ssc.start();
        ssc.awaitTermination();
    }
}

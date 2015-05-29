package com.zdatainc.rts.spark;

import com.zdatainc.rts.model.Properties;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.*;
import scala.Tuple5;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class HTTPNotifierFunction
    implements Function<JavaRDD<Tuple5<Long, String, Float, Float, String>>,
                        Void>
{
    private static final long serialVersionUID = 42l;

    @Override
    public Void call(JavaRDD<Tuple5<Long, String, Float, Float, String>> rdd)
    {
        rdd.foreach(new SendPostFunction());
        return null;
    }
}

class SendPostFunction
    implements VoidFunction<Tuple5<Long, String, Float, Float, String>>
{
    private static final long serialVersionUID = 42l;

    public void call(Tuple5<Long, String, Float, Float, String> tweet)
    {
        String webserver = Properties.getString("rts.spark.webserv");
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(webserver);
        String content = String.format(
            "{\"id\": \"%d\", "     +
            "\"text\": \"%s\", "    +
            "\"pos\": \"%f\", "     +
            "\"neg\": \"%f\", "     +
            "\"score\": \"%s\" }",
            tweet._1(),
            tweet._2(),
            tweet._3(),
            tweet._4(),
            tweet._5());

        try
        {
            post.setEntity(new StringEntity(content));
            HttpResponse response = client.execute(post);
            org.apache.http.util.EntityUtils.consume(response.getEntity());
        }
        catch (Exception ex)
        {
            Logger LOG = Logger.getLogger(this.getClass());
            LOG.error("exception thrown while attempting to post", ex);
            LOG.trace(null, ex);
        }
    }
}

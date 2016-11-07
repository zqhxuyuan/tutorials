package com.zqh.velocity;

import java.io.*;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.VelocityContext;

/**
 * Created by zhengqh on 15/8/13.
 */
public class Example {

    public static void main(String args[]) throws Exception{
        String template = "We are using $project $name to render this.";
        //testFromStr(template);

        testFromFile();
    }

    public static void testFromStr(String template){
        /* first, we init the runtime engine.  Defaults are fine. */
        Velocity.init();

        /* lets make a Context and put data into it */
        VelocityContext context = new VelocityContext();
        context.put("name", "Velocity");
        context.put("project", " Jakarta");

        /* lets render a template */
        StringWriter out = new StringWriter();

        Velocity.evaluate(context, out, "mystring", template);
        System.out.println(out);
    }

    public static void testFromFile() throws Exception{
        StringBuffer sb = new StringBuffer();
        InputStream is = Example.class.getClassLoader().getResourceAsStream("velocity/hello.vm");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        br.close();

        testFromStr(sb.toString());
    }
}

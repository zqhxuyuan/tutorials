package com.zqh.hadoop.mr.GTK;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.ToolRunner;

/**
 * @author Deepika Mohan
 *         <p>
 *         Description : MapReduce program to determine whether a destination node
 *         is reachable from the source.
 *         <p>
 *         The reachability problem can be defined as finding whether there is a path between two given nodes in a graph.
 *         The reachability program is implemented by using Breadth-first
 *         search concept.
 *         <p>
 *         Reference :
 *         http://www.johnandcailin.com/blog/cailin/breadth-first-graph
 *         -search-using-iterative-map-reduce-algorithm
 *         <p>
 *         Hadoop version used : 0.20.2
 */
public class ReachabilityJob extends ExampleBaseJob {


    static enum ReachabilityCounters {
        numberOfIterations, //counter to determine the number of iterations or if more iterations are required
        isDestinationFound //counter to determine if the destination is reached
    }

    /**
     * @author Deepika Mohan
     *         <p>
     *         Description : Mapper class that implements the map part of  Breadth-first search
     *         algorithm as used in the map part of the reachability program. It extends the SearchMapper class.
     *         <p>
     *         Input format : nodeID<tab>list_of_adjacent_nodes|distance_from_the_source|color|parent_node
     *         <p>
     *         Reference :
     *         http://www.johnandcailin.com/blog/cailin/breadth-first-graph
     *         -search-using-iterative-map-reduce-algorithm
     *         <p>
     *         Hadoop version used : 0.20.2
     */

// the type parameters are the input keys type, the input values type, the
// output keys type, the output values type
    public static class ReachabilityMapper extends SearchMapper {


        @Override
        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {

            Node inNode = new Node(value.toString());

            String src = context.getConfiguration().get("source"); //get the source id

            //the source id is passed as a command-line argument, we set the color of the source as GRAY, the distance from the source as 0
            if (inNode.getId().equals(src)) {
                //updating the fields of the source node
                inNode.setColor(Node.Color.GRAY);
                inNode.setDistance(0);
                inNode.setParent("source");
            }

            //call the super class' map method passing the inNode as a parameter
            super.map(key, value, context, inNode);
        }
    }

    /**
     * @author Deepika Mohan
     *         <p>
     *         Description : Reducer class that implements the reduce part of  Reachability
     *         algorithm. This reducer class extends the SearchReducer class. If the destination is found in the list of adjacent nodes during the BFS, then the counter is incremented
     *         <p>
     *         Input format : <nodeID, list_of_adjacent_nodes|distance_from_the_source|color|parent>
     *         Output format : <nodeID, (updated)    list_of_adjacent_nodes|distance_from_the_source|color|parent>
     *         <p>
     *         Reference :
     *         http://www.johnandcailin.com/blog/cailin/breadth-first-graph
     *         -search-using-iterative-map-reduce-algorithm
     *         <p>
     *         Hadoop version used : 0.20.2
     */
    public static class ReachabilityReducer extends SearchReducer {

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {

            //create a new out node and set its values
            Node outNode = new Node();

            //call the reduce method of SearchReducer class
            outNode = super.reduce(key, values, context, outNode);

            String dest = context.getConfiguration().get("destination"); //get the destination node

            //if the color of the node is not WHITE and if the destination is processed, then note that the destination is found by incrementing the counter
            if (outNode.getColor() != Node.Color.WHITE && key.toString().equals(dest)) {

                context.getCounter(ReachabilityCounters.isDestinationFound).increment(1L);
            }

            //if the color of the node is gray, the execution has to continue, this is done by incrementing the counter
            if (outNode.getColor() == Node.Color.GRAY) {
                context.getCounter(ReachabilityCounters.numberOfIterations).increment(1L);
            }
        }
    }


    // method to set the configuration for the job and the mapper and the reducer classes
    private Job getJobConf(String[] args) throws Exception {


        JobInfo jobInfo = new JobInfo() {

            @Override
            public Class<? extends Reducer> getReducerClass() {

                return ReachabilityReducer.class;
            }

            @Override
            public Class<?> getOutputValueClass() {

                return Text.class;
            }

            @Override
            public Class<?> getOutputKeyClass() {

                return Text.class;
            }

            @Override
            public Class<? extends Mapper> getMapperClass() {

                return ReachabilityMapper.class;
            }

            @Override
            public Class<?> getJarByClass() {
                return ReachabilityJob.class;
            }

            @Override
            public Class<? extends Reducer> getCombinerClass() {
                return null;
            }
        };

        return setupJob("reachabilityjob", jobInfo);


    }

    // the driver to execute the job and invoke the map/reduce functions

    @Override
    public int run(String[] args) throws Exception {

        int iterationCount = 0; // counter to set the ordinal number of the intermediate outputs

        Job job;
        long terminationValue = 0;
        long isDestinationReached = 0;

        String src = args[2];
        String dest = args[3];


        //loop till there are more gray nodes to process and the destination is not reached
        while (iterationCount <= terminationValue && isDestinationReached == 0L) {


            job = getJobConf(args); // get the job configuration
            String input;
            if (iterationCount == 0) // for the first iteration , the input will be the input argument
                input = args[0];
            else
                // for the remaining iterations, the input will be the output of the previous iteration
                input = args[1] + iterationCount;

            String output = args[1] + (iterationCount + 1); // setting the output file

            job.getConfiguration().set("source", src); //setting the source to be used in mapper or reducer
            job.getConfiguration().set("destination", dest); //setting the destination to be used in mapper or reducer

            FileInputFormat.setInputPaths(job, new Path(input)); // setting the input files for the job
            FileOutputFormat.setOutputPath(job, new Path(output)); // setting the output files for the job

            job.waitForCompletion(true); // wait for the job to complete

            Counters jobCntrs = job.getCounters();
            terminationValue = jobCntrs.findCounter(ReachabilityCounters.numberOfIterations).getValue(); //get the terminationValue that will determine whether to continue the iteration

            isDestinationReached = jobCntrs.findCounter(ReachabilityCounters.isDestinationFound).getValue();//get the isDestinationReached value that will notify if the destination is reached


            iterationCount++;

        }

        //Check whether there is a path between the source and the destination based on the isDestinationReached counter value and print the result

        if (isDestinationReached == 0L) {
            System.out.println("There is no path between " + src + " and " + dest);
        } else {
            System.out.println("There is a path between " + src + " and " + dest);
        }
        return 0;
    }

    public static void main(String[] args) throws Exception {

        int res = ToolRunner.run(new Configuration(), new ReachabilityJob(), args);
        if (args.length != 4) {
            System.err.println("Usage: <in> <out> <sourceNode> <destinationNode>");
        }
        System.exit(res);
    }

}

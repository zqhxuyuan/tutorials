/*
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
package com.zqh.giraph.standalone;

import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.NullWritable;

import java.io.IOException;
import org.apache.giraph.edge.Edge;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;

@SuppressWarnings("rawtypes")
public class TriangleCounting extends BasicComputation<Text, Text, NullWritable, Text> {

    /**
     * Somma aggregator name
     */
    private static final String SOMMA = "somma";

    @Override
    public void compute(Vertex<Text, Text, NullWritable> vertex,
            Iterable<Text> messages) throws IOException {
        if (getSuperstep() == 0) {
            //costruisco Text lista vicini
            Text neigborhood = new Text();
            Iterable<Edge<Text, NullWritable>> edges = vertex.getEdges();
            for (Edge<Text, NullWritable> edge : edges) {
                neigborhood.set(neigborhood.toString() + "-" + edge.getTargetVertexId().toString());
            }
            for (Edge<Text, NullWritable> edge : edges) {
                this.sendMessage(edge.getTargetVertexId(), neigborhood);
            }
        } else if (getSuperstep() == 1) {
            Double T = 0.0;
            //confronto edge "mancanti" inviati con lista vicini nodo
            for (Text message : messages) {
                String[] msgSplit = message.toString().split("-");//lista neigbohood
                Iterable<Edge<Text, NullWritable>> edges = vertex.getEdges();
                for (Edge<Text, NullWritable> edge : edges) {
                    for (String missEdge : msgSplit) {
                        if (missEdge.equals(edge.getTargetVertexId().toString())) {
                            T++;
                        }
                    }
                }
            }

            T = T / 6;
            vertex.setValue(new Text(T.toString()));
            vertex.voteToHalt();

            aggregate(SOMMA, new DoubleWritable(T));
//            getAggregatedValue(SOMMA);
//            System.out.println("DEBUG "+ getAggregatedValue(SOMMA));

        }

    }


}

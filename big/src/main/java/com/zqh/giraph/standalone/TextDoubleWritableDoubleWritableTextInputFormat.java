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

import org.apache.giraph.edge.Edge;
import org.apache.giraph.edge.EdgeFactory;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.giraph.io.formats.TextVertexInputFormat;
import org.apache.hadoop.io.DoubleWritable;

/**
 * Simple text-based {@link org.apache.giraph.io.VertexInputFormat} for
 * unweighted graphs with long ids.
 *
 * Each line consists of: vertex neighbor1 neighbor2 ...
 */
public class TextDoubleWritableDoubleWritableTextInputFormat extends
    TextVertexInputFormat<Text, DoubleWritable, DoubleWritable> {
  /** Separator of the vertex and neighbors */
//  private static final Pattern SEPARATOR = Pattern.compile("[\t ]");
    
  private static final Pattern SEPARATOR = Pattern.compile(" ");

  @Override
  public TextVertexInputFormat.TextVertexReader createVertexReader(InputSplit split,
                                             TaskAttemptContext context)
    throws IOException {
    return new TextDoubleWritableDoubleWritableVertexReader();
  }

  /**
   * Vertex reader associated with LongLongNullLongTextInputFormat.
   */
  public class TextDoubleWritableDoubleWritableVertexReader extends
      TextVertexReaderFromEachLineProcessed<String[]> {
    /** Cached vertex id for the current line */
    private Text id;
    private DoubleWritable value;

    @Override
    protected String[] preprocessLine(Text line) throws IOException {
      String[] tokens = SEPARATOR.split(line.toString());
      id = new Text(tokens[0]);
      value = new DoubleWritable(0);
      return tokens;
    } 

    @Override
    protected Text getId(String[] tokens) throws IOException {
      return id;
    }

    @Override
    protected DoubleWritable getValue(String[] tokens) throws IOException {
      return value;
    }

    @Override
    protected List<Edge<Text, DoubleWritable>> getEdges(
        String[] tokens) throws IOException {
      List<Edge<Text, DoubleWritable>> edges =
          Lists.newArrayListWithCapacity(tokens.length - 1);
      for (int n = 1; n < tokens.length; n++) {
        edges.add(EdgeFactory.create(
            new Text(tokens[n]), new DoubleWritable(1d)));
      }
      return edges;
    }
  }
}

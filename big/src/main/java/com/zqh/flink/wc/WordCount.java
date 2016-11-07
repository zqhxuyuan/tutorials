package com.zqh.flink.wc;

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

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

/**
 * Implements the "WordCount" program that computes
 * a simple word occurrence histogram over some sample data
 *
 * <p>
 * This example shows how to:
 * <ul>
 * <li>write a simple Flink program.
 * <li>use Tuple data types.
 * <li>write and use user-defined functions.
 * </ul>
 *
 * 运行方式: 不需要启动hadoop, flink任何进程, 直接运行测试类
 * 输出结果:
 2> (and,1)
 2> (not,1)
 2> (take,1)
 2> (to,4)
 3> (a,1)
 3> (against,1)
 3> (is,1)
 3> (or,2)
 3> (outrageous,1)
 3> (question,1)
 3> (slings,1)
 3> (that,1)
 3> (troubles,1)
 1> (arms,1)
 1> (arrows,1)
 1> (be,2)
 1> (in,1)
 1> (nobler,1)
 1> (suffer,1)
 1> (the,3)
 1> (whether,1)
 4> (fortune,1)
 4> (mind,1)
 4> (of,2)
 4> (sea,1)
 4> (tis,1)
 */
public class WordCount {

	public static void main(String[] args) throws Exception {

		// set up the execution environment
		final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

		// get input data
		DataSet<String> text = env.fromElements(
				"To be, or not to be,--that is the question:--",
				"Whether 'tis nobler in the mind to suffer",
				"The slings and arrows of outrageous fortune",
				"Or to take arms against a sea of troubles,"
				);

		DataSet<Tuple2<String, Integer>> counts =
				// split up the lines in pairs (2-tuples) containing: (word,1)
				text.flatMap(new LineSplitter())
				// group by the tuple field "0" and sum up tuple field "1"
				.groupBy(0)
				.sum(1);

		// emit result
		counts.print();

		// execute program
		env.execute("WordCount Example");
	}

}

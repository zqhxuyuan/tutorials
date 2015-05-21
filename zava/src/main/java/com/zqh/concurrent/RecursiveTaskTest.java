package com.zqh.concurrent;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RecursiveTaskTest {

    static class MaximunNumberSolver extends RecursiveTask<Integer> {

        private static final long serialVersionUID = -6080276473647416925L;
        private static final Log LOG = LogFactory.getLog(MaximunNumberSolver.class);
        private static final int SEQUENTIAL_THRESHOLD = 100;
        private final int[] data;
        private final int startIndex;
        private final int endIndex;

        public MaximunNumberSolver(int[] data, int start, int end) {
            this.data = data;
            this.startIndex = start;
            this.endIndex = end;
        }

        public MaximunNumberSolver(int[] data) {
            this(data, 0, data.length);
        }

        @Override
        protected Integer compute() {
            final int length = endIndex - startIndex;
            if (length < SEQUENTIAL_THRESHOLD) {
                return computeMax();
            }
            // left part
            final int split = length / 2;
            final MaximunNumberSolver leftSolver = new MaximunNumberSolver(data, startIndex, startIndex + split);
            leftSolver.fork();

            // right part
            final MaximunNumberSolver rightSolver = new MaximunNumberSolver(data, startIndex + split, endIndex);
            rightSolver.fork();
            return Math.max(rightSolver.join(), leftSolver.join());
        }

        private Integer computeMax() {
            LOG.debug(Thread.currentThread() + " computing: " + startIndex + " to " + endIndex);
            int max = Integer.MIN_VALUE;
            for (int i = startIndex; i < endIndex; i++) {
                if (data[i] > max) {
                    max = data[i];
                }
            }
            return max;
        }

        public static void main(String[] args) {
            // create a random data set
            final int[] data = new int[1000000];
            final Random random = new Random();
            for (int i = 0; i < data.length; i++) {
                data[i] = random.nextInt(10000000);
            }

            // submit the task to the pool
            final ForkJoinPool pool = new ForkJoinPool(8);
            final MaximunNumberSolver solver = new MaximunNumberSolver(data);
            LOG.info(pool.invoke(solver));
        }
    }

}
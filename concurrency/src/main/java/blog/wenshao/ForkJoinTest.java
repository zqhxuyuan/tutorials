package blog.wenshao;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Created by zqhxuyuan on 15-5-13.
 */
public class ForkJoinTest {

    public static void main(String[] args) throws Exception {
        final ForkJoinPool mainPool = new ForkJoinPool();
        int len = 1000 * 1000 * 10;
        int[] array = new int[len];
        mainPool.invoke(new SortTask(array, 0, len - 1));
    }

    public static class SortTask extends RecursiveAction {
        private int[] array;
        private int fromIndex;
        private int toIndex;
        private final int chunksize = 1024;

        public SortTask(int[] array, int fromIndex, int toIndex) {
            this.array = array;
            this.fromIndex = fromIndex;
            this.toIndex = toIndex;
        }
        @Override
        protected void compute() {
            int size = toIndex - fromIndex + 1;
            if (size < chunksize) {
                Arrays.sort(array, fromIndex, toIndex);
            } else {
                int leftSize = size / 2;
                SortTask leftTask = new SortTask(array, fromIndex, fromIndex + leftSize);
                SortTask rightTask = new SortTask(array, fromIndex + leftSize + 1, toIndex);
                this.invokeAll(leftTask, rightTask);
            }
        }
    }
}

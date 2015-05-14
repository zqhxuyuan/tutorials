package blog.xylz;

public class ReorderingDemo {
    static int x = 0, y = 0, a = 0, b = 0;

    /**
     * A       B       C      D
     * a=1    b=1     a=1     a=1   还没刷新到主内存
     * x=b=0  y=a=0   b=1     x=b=0
     * b=1    a=1     x=b=1   b=1
     * y=a=1  x=b=1   y=a=1   y=a=0 取到的仍然是0:
     * (0 1)  (1 0)   (1 1)   (0 0)
     *
     * A: one先执行, two后执行
     * B: two先执行, one后执行
     * C: one和two交替执行
     * D: 由于线程one执行a=1完成后还没有来得及将数据1写回主存(这时候数据是在线程one的堆栈里面的),线程two从主存中拿到的数据a可能仍然是0
     */
    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 100; i++) {
            x = y = a = b = 0;

            Thread one = new Thread() {
                public void run() {
                    a = 1;
                    x = b;
                }
            };
            Thread two = new Thread() {
                public void run() {
                    b = 1;
                    y = a;
                }
            };

            one.start();
            two.start();
            one.join();
            two.join();
            System.out.println(x + " " + y);
        }
    }
}
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Created by zhengqh on 15/11/4.
 */
class HelloWorld {

    public static void main(String[] args) {
        println("HelloWorld Just Like Java...")

    }

    static void io(String dir, String fileName){
        new File(dir, fileName).eachLine { line, nb ->
            println "Line $nb : $line"
        }

        //resource auto closed
        def count = 0
        def MAX_SIZE = 3
        new File(dir, fileName).withReader { reader ->
            while(reader.readLine()){
                if(++count > MAX_SIZE){
                    throw new RuntimeException("should not large than 3 times")
                }
            }
        }

        def list = new File(dir, fileName).collect {it}

        def array = new File(dir, fileName) as String[]

        def bytes = new File(dir, fileName).bytes

        def is = new File(dir, fileName).newInputStream()
        def stringList = is.readLines()
        is.close()

        new File(dir, fileName).withInputStream {stream ->
            is.readLines()
        }

        new File(dir, fileName).withWriter('utf-8') {writer ->
            writer.writeLine 'Hello'
            writer.write(1)
        }

        new File(dir, fileName) << '''Hello 2
        a frog
        '''
    }

    static void helloWorld(){
        //the method will be chosen based on the types of the arguments at runtime
        Object o = "Object";
        int result = method(o);
        println(result)

        int[] array = [1,2,3]

        //ARM blocks
        new File('/Users/zhengqh/sh/map.exp').eachLine('UTF-8') {
            println it
        }
        new File('/Users/zhengqh/sh/map.exp').withReader('UTF-8') { reader ->
            reader.eachLine {
                println it
            }
        }

        //Anonymous Inner Classes
        CountDownLatch called = new CountDownLatch(1)

        Timer timer = new Timer()
        timer.schedule(new TimerTask() {
            void run() {
                called.countDown()
            }
        }, 0)

        called.await(10, TimeUnit.SECONDS)

        //Lambdas
        Runnable run = { println 'run' }
        array.each { println it } // or list.each(this.&println)
    }

    static int method(String arg) {
        return 1;
    }
    static int method(Object arg) {
        return 2;
    }
}

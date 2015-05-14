package blog.xylz;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * AtomicXXXFieldUpdater是基于反射的原子更新字段的值
 *
 * AtomicIntegerFieldUpdater AtomicLongFieldUpdater AtomicReferenceFieldUpdater
 *
 * 原子更新基本类型   AtomicInteger
 * 原子更新数组      AtomicIntegerArray  AtomicReferenceArray
 * 原子更新引用      AtomicReference
 * 原子更新字段      AtomicReferenceFieldUpdater
 *
 */
public class AtomicIntegerFieldUpdaterDemo {

    /**
     * 字段必须是 volatile 类型的
     * 字段的描述类型是与调用者与操作对象字段的关系一致
     * 只能是实例变量,不能是类变量,也就是说不能加 static 关键字
     * 只能是可修改变量,不能使 final 变量
     * 对于 AtomicIntegerFieldUpdater 和 AtomicLongFieldUpdater 只能修改 int/long 类型的字段,不能修改其包装类型(Integer/Long)。
     *   如果要修改包装类型就需要使用 AtomicReferenceFieldUpdater
     */
    class DemoData{
        public volatile int value1 = 1;
        volatile int value2 = 2;
        //DemoData 的字段 value3/value4 对于 AtomicIntegerFieldUpdaterDemo 类是不可见的,因此通过反射是不能直接修改其值的
        protected volatile int value3 = 3;
        private volatile int value4 = 4;
    }

    //原子类型的int字段更新器
    AtomicIntegerFieldUpdater<DemoData> getUpdater(String fieldName) {
        return AtomicIntegerFieldUpdater.newUpdater(DemoData.class, fieldName);
    }

    void doit() {
        DemoData data = new DemoData();
        System.out.println("1 ==> "+getUpdater("value1").getAndSet(data, 10));
        System.out.println("3 ==> "+getUpdater("value2").incrementAndGet(data));
        //Class AtomicIntegerFieldUpdaterDemo can not access a protected member of class com.xylz
        System.out.println("2 ==> "+getUpdater("value3").decrementAndGet(data));
        System.out.println("true ==> "+getUpdater("value4").compareAndSet(data, 4, 5));
    }

    public static void main(String[] args) {
        AtomicIntegerFieldUpdaterDemo demo = new AtomicIntegerFieldUpdaterDemo();
        demo.doit();
    }
}
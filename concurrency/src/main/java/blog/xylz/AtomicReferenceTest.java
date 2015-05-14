package blog.xylz;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 原子更新基本类型的AtomicInteger，只能更新一个变量，
 * 如果要原子的更新多个变量，就需要使用这个原子更新引用类型提供的类: AtomicReference
 */
public class AtomicReferenceTest {

    public static AtomicReference<User> atomicUserRef = new AtomicReference<User>();

    public static void main(String[] args) {
        User user = new User("conan", 15);
        User updateUser = new User("Shinichi", 17);

        atomicUserRef.set(user);
        atomicUserRef.compareAndSet(user, updateUser);

        System.out.println(atomicUserRef.get().getName());
        System.out.println(atomicUserRef.get().getOld());
    }

    static class User {
        private String name;
        private int old;

        public User(String name, int old) {
            this.name = name;
            this.old = old;
        }

        public String getName() {
            return name;
        }

        public int getOld() {
            return old;
        }
    }
}
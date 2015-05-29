package com.zqh.java8;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Created by zqhxuyuan on 15-5-24.
 *
 * https://chou.it/2014/03/java-8-new-features/
 */
public class NewWorld {

    public static List<String> names = Arrays.asList("Tan", "Zhen", "Yu");

    public static void main(String[] args) {
        functionInterface();
    }

    // java way
    public static void list1(){
        Collections.sort(names, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return a.compareTo(b);
            }
        });
        for (String name : names) {
            System.out.println(name);
        }
    }

    // 打印list形成的流
    public static void printStream(List<String> names){
        Stream<String> stream = names.stream();
        stream.forEach(System.out::println);
    }

    // 函数体, 返回值
    public static void list2(){
        Collections.sort(names, (String a, String b) -> {
            return a.compareTo(b);
        });
        printStream(names);
    }

    // 只有一条语句的函数体, 省略大括号, 没有return
    public static void list3(){
        Collections.sort(names, (String a, String b) -> a.compareTo(b));
        printStream(names);
    }

    // 类型自动推导
    public static void list4(){
        Collections.sort(names, (a, b) -> a.compareTo(b));
        printStream(names);
    }

    // 接口中可以定义默认方法, 即接口中可以有方法的实现
    public static void defaultMethod(){
        //接口Formula定义了一个默认方法sqrt可以直接被formula的实例包括匿名对象访问到
        Formula formula = new Formula() {
            @Override
            public double calculate(int a) {
                return sqrt(a * 100);
            }
        };

        formula.calculate(100);     // 100.0
        formula.sqrt(16);           // 4.0

        //在lambda表达式中没有办法访问到接口的默认方法
        //Formula formula2 = (a) -> sqrt( a * 100);
    }

    // 下面的(from) -> Integer.valueOf(from)就是一个lambda表达式, 对应了Converter函数式接口类型
    public static void functionInterface(){
        Converter<String, Integer> converter = (from) -> Integer.valueOf(from);
        Integer converted = converter.convert("123");
        System.out.println(converted);    // 123
        converter.defMethod();  // default method @ function interface
    }

    // 访问局部变量
    public static void lambdaScope(){
        final int num = 1;
        Converter<Integer, String> stringConverter = (from) -> String.valueOf(from + num);
        stringConverter.convert(2);     // 3

        //可以不声明为final, 但是不可以修改
        int num2 = 1;
        Converter<Integer, String> stringConverter2 = (from) -> String.valueOf(from + num2);
        stringConverter2.convert(2);    // 3
        //num2 = 2;
    }

    // 访问对象字段与静态变量
    static int outerStaticNum;
    int outerNum;

    void testScopes() {
        Converter<Integer, String> stringConverter1 = (from) -> {
            outerNum = 23;
            return String.valueOf(from);
        };

        Converter<Integer, String> stringConverter2 = (from) -> {
            outerStaticNum = 72;
            return String.valueOf(from);
        };
    }

    // 方法引用: 使用::关键字来传递方法或者构造函数引用
    public static void staticMethodRef(){
        Converter<String, Integer> converter = Integer::valueOf;
        Integer converted = converter.convert("123");
        System.out.println(converted);   // 123
    }

    // 构造方法引用
    public static void constructMethodRef(){
        //使用Person::new来获取Person类构造函数的引用
        PersonFactory<Person> personFactory = Person::new;

        //Java编译器会自动根据PersonFactory.create方法的签名来选择合适的构造函数
        Person person = personFactory.create("Peter", "Parker");
    }

    public static void advanceInterface(){
        Predicate<String> predicate = (s) -> s.length() > 0;
        predicate.test("foo");              // true
        predicate.negate().test("foo");     // false

        Predicate<Boolean> nonNull = Objects::nonNull;
        Predicate<Boolean> isNull = Objects::isNull;

        Predicate<String> isEmpty = String::isEmpty;
        Predicate<String> isNotEmpty = isEmpty.negate();

        List languages = Arrays.asList("Java", "Scala", "C++", "Haskell", "Lisp");

        System.out.println("Languages which starts with J :");
        predicate = (str) -> str.startsWith("J");
        filter(languages, predicate);

        System.out.println("Languages which ends with a ");
        predicate = (str) -> str.endsWith("a");
        filter(languages, predicate);

        System.out.println("Print language whose length greater than 4:");
        predicate = (str) -> str.length() > 4;
        filter(languages, predicate);

        System.out.println("Print all languages :");
        filter(languages, (str) -> true);

        System.out.println("Print no language : ");
        filter(languages, (str) -> false);

        //函数
        Function<String, Integer> toInteger = Integer::valueOf;
        Function<String, String> backToString = toInteger.andThen(String::valueOf);
        backToString.apply("123");     // "123"

        //提供一个生成器
        Supplier<Person> personSupplier = Person::new;
        personSupplier.get();   // new Person

        //消费者
        Consumer<Person> greeter = (p) -> System.out.println("Hello, " + p.firstName);
        greeter.accept(new Person("Luke", "Skywalker"));

        //比较器
        Comparator<Person> comparator = (p1, p2) -> p1.firstName.compareTo(p2.firstName);
        Person p1 = new Person("John", "Doe");
        Person p2 = new Person("Alice", "Wonderland");
        comparator.compare(p1, p2);             // > 0
        comparator.reversed().compare(p1, p2);  // < 0

        //可选
        Optional<String> optional = Optional.of("bam");
        optional.isPresent();           // true
        optional.get();                 // "bam"
        optional.orElse("fallback");    // "bam"
        optional.ifPresent((s) -> System.out.println(s.charAt(0)));     // "b"

        //流接口
        List<String> stringCollection = new ArrayList<>();
        stringCollection.add("ddd2");
        stringCollection.add("aaa2");
        stringCollection.add("bbb1");
        stringCollection.add("aaa1");
        stringCollection.add("bbb3");
        stringCollection.add("ccc");
        stringCollection.add("bbb2");
        stringCollection.add("ddd1");

        stringCollection
                .stream()
                .filter((s) -> s.startsWith("a"))
                .forEach(System.out::println);  // "aaa2", "aaa1"

        stringCollection
                .stream()
                .sorted()
                .filter((s) -> s.startsWith("a"))
                .forEach(System.out::println);  // "aaa1", "aaa2"

        stringCollection
                .stream()
                .map(String::toUpperCase)
                .sorted((a, b) -> b.compareTo(a))
                .forEach(System.out::println);  // "DDD2", "DDD1", "CCC", "BBB3", "BBB2", "AAA2", "AAA1"

        boolean anyStartsWithA = stringCollection
                        .stream()
                        .anyMatch((s) -> s.startsWith("a"));
        System.out.println(anyStartsWithA);      // true

        boolean allStartsWithA = stringCollection
                        .stream()
                        .allMatch((s) -> s.startsWith("a"));
        System.out.println(allStartsWithA);      // false

        boolean noneStartsWithZ = stringCollection
                        .stream()
                        .noneMatch((s) -> s.startsWith("z"));
        System.out.println(noneStartsWithZ);      // true

        long startsWithB = stringCollection
                        .stream()
                        .filter((s) -> s.startsWith("b"))
                        .count();
        System.out.println(startsWithB);            // 3

        Optional<String> reduced = stringCollection
                        .stream()
                        .sorted()
                        .reduce((s1, s2) -> s1 + "#" + s2);
        reduced.ifPresent(System.out::println); // "aaa1#aaa2#bbb1#bbb2#bbb3#ccc#ddd1#ddd2"

        //对于20元以上的商品，进行9折处理，最后得到这些商品的折后价格
        List<BigDecimal> prices = Arrays.asList(BigDecimal.valueOf(11), BigDecimal.valueOf(31), BigDecimal.valueOf(40));
        final BigDecimal totalOfDiscountedPrices = prices.stream()
                .filter(price -> price.compareTo(BigDecimal.valueOf(20)) > 0)
                .map(price -> price.multiply(BigDecimal.valueOf(0.9)))
                .reduce(BigDecimal.ZERO,BigDecimal::add);

        System.out.println("Total of discounted prices: " + totalOfDiscountedPrices);

        int max = 1000000;
        List<String> values = new ArrayList<>(max);
        for (int i = 0; i < max; i++) {
            UUID uuid = UUID.randomUUID();
            values.add(uuid.toString());
        }

        long t0 = System.nanoTime();
        long count = values.stream().sorted().count();
        System.out.println(count);
        long t1 = System.nanoTime();
        long millis = TimeUnit.NANOSECONDS.toMillis(t1 - t0);
        System.out.println(String.format("sequential sort took: %d ms", millis));   // 串行耗时: 899 ms

        t0 = System.nanoTime();
        count = values.parallelStream().sorted().count();
        System.out.println(count);
        t1 = System.nanoTime();
        millis = TimeUnit.NANOSECONDS.toMillis(t1 - t0);
        System.out.println(String.format("parallel sort took: %d ms", millis));     // 并行排序耗时: 472 ms
    }

    public static void filter(List names, Predicate condition) {
        names.stream().filter(
                (name) -> (condition.test(name))
        ).forEach(
                (name) -> {
                    System.out.println(name + " ");
                }
        );
    }

    public static void testMap(){
        Map<Integer, String> map = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            map.putIfAbsent(i, "val" + i);
        }
        map.forEach((id, val) -> System.out.println(val));

        map.computeIfPresent(3, (num, val) -> val + num);
        map.get(3);             // val33

        map.computeIfPresent(9, (num, val) -> null);
        map.containsKey(9);     // false

        map.computeIfAbsent(23, num -> "val" + num);
        map.containsKey(23);    // true

        map.computeIfAbsent(3, num -> "bam");
        map.get(3);             // val33

        map.remove(3, "val3");
        map.get(3);             // val33

        map.remove(3, "val33");
        map.get(3);             // null

        map.getOrDefault(42, "not found");  // not found

        map.merge(9, "val9", (value, newValue) -> value.concat(newValue));
        map.get(9);             // val9

        map.merge(9, "concat", (value, newValue) -> value.concat(newValue));
        map.get(9);             // val9concat
    }
}

//----------------------------------------------------------------

// 接口中可以定义方法的实现: 默认方法
interface Formula {
    double calculate(int a);

    default double sqrt(int a) {
        return Math.sqrt(a);
    }
}

// 函数式接口: 每一个lambda表达式都对应一个类型, 通常是接口类型
@FunctionalInterface
interface Converter<F, T> {
    //“函数式接口”是指仅仅只包含一个抽象方法的接口，每一个该类型的lambda表达式都会被匹配到这个抽象方法
    T convert(F from);

    //如果再定义一个抽象方法(没有实现),则@FunctionalInterface会报错
    //T convert2(F from);

    //因为"默认方法"不算抽象方法，所以你也可以给你的函数式接口添加默认方法
    default void defMethod(){
        System.out.println("default method @ function interface");
    }
}

class Person {
    String firstName;
    String lastName;

    Person() {}

    Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}

interface PersonFactory<P extends Person> {
    P create(String firstName, String lastName);
}

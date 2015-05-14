package blog.iter_zc.jvm;

public class SizeofWithInstrumetation {
    private static class ObjectA {
        String str;  // 4
        int i1; // 4
        byte b1; // 1
        byte b2; // 1
        int i2;  // 4
        ObjectB obj; //4
        byte b3;  // 1
    }

    private static class ObjectB {

    }

    public static void main(String[] args){
        System.out.println(ObjectShallowSize.sizeOf(new ObjectA()));
    }
}
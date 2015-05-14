package blog.iter_zc.jvm;

public class ClassIntrospectorTest
{
    public static void main(String[] args) throws IllegalAccessException {
        final ClassIntrospector ci = new ClassIntrospector();

        ObjectInfo res;

        res = ci.introspect( new ObjectA() );
        System.out.println( res.getDeepSize() );

        res = ci.introspect( new ObjectC() );
        System.out.println( res.getDeepSize() );
    }

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

    private static class ObjectC {
        ObjectD[] array = new ObjectD[2];

        public ObjectC(){
            array[0] = new ObjectD();
            array[1] = new ObjectD();
        }
    }

    private static class ObjectD {
        int value;
    }
}
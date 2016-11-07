package org.tguduru.guava.supplier;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import org.tguduru.guava.domain.Color;
import org.tguduru.guava.domain.MobilePhone;
import org.tguduru.guava.domain.OS;

/**
 * Demonstrates the usage of {@link com.google.common.base.Supplier} and explains the differences with
 * {@link java.util.function.Supplier}
 * @author Guduru, Thirupathi Reddy
 */
public class SupplierDemo {
    public static void main(final String[] args) {
        // this will create a new instance every time you call get()
        System.out.println(new IPhoneSupplier().get());
        // lets say you want the same instance (singleton) then user Suppliers.memoize
        final Supplier<MobilePhone> mobilePhoneSupplier = Suppliers.memoize(new IPhoneSupplier());
        MobilePhone mobilePhone1 = mobilePhoneSupplier.get();
        MobilePhone mobilePhone2 = mobilePhoneSupplier.get();
        System.out.println("isSingleton : "+mobilePhone1.equals(mobilePhone2));// should print true as both get()s return the same
                                                              // instance.

        // java 8 way
        System.out.println("*** Java 8 ***");
        java.util.function.Supplier<MobilePhone> javaAndroidPhoneSupplier = () -> new MobilePhone("nexus s",
                Color.BLACK, OS.ANDROID);
        System.out.println(javaAndroidPhoneSupplier.get());
    }
}

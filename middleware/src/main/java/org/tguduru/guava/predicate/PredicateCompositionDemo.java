package org.tguduru.guava.predicate;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;

import org.tguduru.guava.domain.Color;
import org.tguduru.guava.domain.MobilePhone;
import org.tguduru.guava.domain.OS;

import java.util.List;

/**
 * Demonstrates the composition of {@link com.google.common.base.Predicate}
 * @author Guduru, Thirupathi Reddy
 */
public class PredicateCompositionDemo {
    public static void main(final String[] args) {
        final Predicate<MobilePhone> iosPredicate = new Predicate<MobilePhone>() {
            @Override
            public boolean apply(final MobilePhone input) {
                return input.getOs().equals(OS.IOS);
            }
        };
        final Predicate<MobilePhone> androidPredicate = new Predicate<MobilePhone>() {
            @Override
            public boolean apply(final MobilePhone input) {
                return input.getOs().equals(OS.ANDROID);
            }
        };

        // And predicate
        final Predicate<MobilePhone> andPhonePredicate = Predicates.and(iosPredicate, androidPredicate);

        // Or predicate
        final Predicate<MobilePhone> orPhonePredicate = Predicates.or(iosPredicate, androidPredicate);

        final MobilePhone iPhone5s = new MobilePhone("iPhone5s", Color.WHITE, OS.IOS);
        final MobilePhone galaxyS6 = new MobilePhone("GalaxyS6", Color.BLACK, OS.ANDROID);
        final MobilePhone lumia950 = new MobilePhone("Lumia950", Color.GREY, OS.WINDOWS);
        final List<MobilePhone> mobilePhones = Lists.newArrayList(iPhone5s, galaxyS6, lumia950);

        // usage of AND Predicate
        for (final MobilePhone mobilePhone : mobilePhones) {
            if (andPhonePredicate.apply(mobilePhone))
                System.out.println(mobilePhone);
        }

        // usage of OR Predicate
        for (final MobilePhone mobilePhone : mobilePhones) {
            if (orPhonePredicate.apply(mobilePhone))
                System.out.println(mobilePhone);
        }

        // Predicate Composition, here Predicates,compose will take a Function and a Predicate, will apply Function
        // first and then apply Predicate to return the value.
        final Function<MobilePhone,String> mobilePhoneFunction = new Function<MobilePhone, String>() {
            @Override
            public String apply(final MobilePhone input) {
                return input.getOs().name();
            }
        };

        final Predicate<String> stringPredicate = new Predicate<String>() {
            @Override
            public boolean apply(final String input) {
                return input.equals(OS.IOS.name());
            }
        };

        System.out.println(" **** Predicate Compose *** ");
        final Predicate<MobilePhone> compositePredicate = Predicates.compose(stringPredicate,mobilePhoneFunction);
        for(final MobilePhone mobilePhone : mobilePhones){
            if(compositePredicate.apply(mobilePhone))
                System.out.println(mobilePhone);
        }
        //forEach lambda for filtering values using Predicate.
        mobilePhones.stream().filter(a -> compositePredicate.apply(a)).forEach(System.out::println);

        //java 8 way
        System.out.println("***  Java 8 Implementation ***");
        mobilePhones.stream().filter(a -> a.getOs().equals(OS.IOS)).forEach(System.out::println);
     }
}

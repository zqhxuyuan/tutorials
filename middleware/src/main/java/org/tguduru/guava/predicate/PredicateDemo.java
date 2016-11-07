package org.tguduru.guava.predicate;

import com.google.common.collect.Lists;

import org.tguduru.guava.domain.Color;
import org.tguduru.guava.domain.MobilePhone;
import org.tguduru.guava.domain.OS;

import java.util.List;

/**
 * Demonstrates the usage of {@link com.google.common.base.Predicate}.
 * @author Guduru, Thirupathi Reddy
 */
public class PredicateDemo {
    public static void main(final String[] args) {
        final MobilePhone iPhone5s = new MobilePhone("iPhone5s", Color.WHITE, OS.IOS);
        final MobilePhone galaxyS6 = new MobilePhone("GalaxyS6", Color.BLACK, OS.ANDROID);
        final MobilePhone lumia950 = new MobilePhone("Lumia950", Color.GREY, OS.WINDOWS);
        final List<MobilePhone> mobilePhones = Lists.newArrayList(iPhone5s, galaxyS6, lumia950);
        final IOSPredicate iosPredicate = new IOSPredicate();
        for (final MobilePhone mobilePhone : mobilePhones) {
            if (iosPredicate.apply(mobilePhone))
                System.out.println(mobilePhone);
        }

        // java 8 implementation, this is very simple and easy to understand
        mobilePhones.stream().filter(m -> m.getOs().equals(OS.IOS)).forEach(System.out::println);
    }
}

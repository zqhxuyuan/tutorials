package org.tguduru.guava.function.composition;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import org.tguduru.guava.domain.Color;
import org.tguduru.guava.domain.ManufacturedBy;
import org.tguduru.guava.domain.MobilePhone;
import org.tguduru.guava.domain.OS;

import java.util.Map;

/**
 * Demonstrates the usage of {@link com.google.common.base.Functions#compose(Function, Function)}.
 * @author Guduru, Thirupathi Reddy
 * @modified 11/12/15
 */
public class FunctionalCompositionDemo {
    public static void main(final String[] args) {

        // lets build a data set with phones data.
        final Map<ManufacturedBy, MobilePhone> mobilePhones = Maps.newHashMap();
        final MobilePhone iPhone5s = new MobilePhone("iPhone5s", Color.WHITE, OS.IOS);
        final MobilePhone galaxyS6 = new MobilePhone("GalaxyS6", Color.BLACK, OS.ANDROID);
        final MobilePhone lumia950 = new MobilePhone("Lumia950", Color.GREY, OS.WINDOWS);
        mobilePhones.put(ManufacturedBy.APPLE, iPhone5s);
        mobilePhones.put(ManufacturedBy.SAMSUNG, galaxyS6);
        mobilePhones.put(ManufacturedBy.MICROSOFT, lumia950);

        // now apply the function composition on the above data.
        final Function<MobilePhone, String> mobilePhoneStringFunction = new Function<MobilePhone, String>() {
            @Override
            public String apply(final MobilePhone input) {
                return input.getModel();
            }
        };

        final Function<ManufacturedBy, MobilePhone> manufacturedByListFunction = Functions.forMap(mobilePhones);
        // the functional composition works as follows in a mathematical notation.
        // If f: a->b and g:b->c then h(a) = g(f(a)).
        final Function<ManufacturedBy, String> manufacturedByStringFunction = Functions.compose(mobilePhoneStringFunction,
                manufacturedByListFunction);
        System.out.println(manufacturedByStringFunction.apply(ManufacturedBy.APPLE));
    }
}

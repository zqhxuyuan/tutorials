package org.tguduru.guava.predicate;

import com.google.common.base.Predicate;

import org.tguduru.guava.domain.MobilePhone;
import org.tguduru.guava.domain.OS;

/**
 * Guava's {@link com.google.common.base.Predicate} is used to filter objects from a collections or any other types.
 * In this example it will filter out all iOS devices.
 * @author Guduru, Thirupathi Reddy
 */
public class IOSPredicate implements Predicate<MobilePhone> {
    /**
     * Returns the result of applying this predicate to {@code input}. This method is <i>generally
     * expected</i>, but not absolutely required, to have the following properties:
     * <ul>
     * <li>Its execution does not cause any observable side effects.
     * <li>The computation is <i>consistent with equals</i>; that is, {@link Objects#equal
     * Objects.equal}{@code (a, b)} implies that {@code predicate.apply(a) == predicate.apply(b))}.
     * </ul>
     * @param input
     * @throws NullPointerException if {@code input} is null and this predicate does not accept null
     *             arguments
     */
    @Override
    public boolean apply(final MobilePhone input) {
        return input.getOs().equals(OS.IOS);
    }
}

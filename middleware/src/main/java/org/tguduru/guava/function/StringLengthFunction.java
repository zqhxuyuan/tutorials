package org.tguduru.guava.function;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

/**
 * Implementation of a {@link Function}
 * @author Guduru, Thirupathi Reddy
 */
public class StringLengthFunction implements Function<String, Long> {
    /**
     * Returns the result of applying this function to {@code input}. This method is <i>generally
     * expected</i>, but not absolutely required, to have the following properties:
     * <ul>
     * <li>Its execution does not cause any observable side effects.
     * <li>The computation is <i>consistent with equals</i>; that is, {@link com.google.common.base.Objects#equal
     * Objects.equal}{@code (a, b)} implies that {@code Objects.equal(function.apply(a), function.apply(b))}.
     * </ul>
     * @param input
     * @throws NullPointerException if {@code input} is null and this function does not accept null
     *             arguments
     */
    @Override
    public Long apply(final String input) {
        Preconditions.checkNotNull(input);
        return Long.valueOf(input.length());
    }
}

package org.tguduru.guava.supplier;

import com.google.common.base.Supplier;
import org.tguduru.guava.domain.Color;
import org.tguduru.guava.domain.MobilePhone;
import org.tguduru.guava.domain.OS;

/**
 * @author Guduru, Thirupathi Reddy
 */
public class IPhoneSupplier implements Supplier<MobilePhone> {
    /**
     * Retrieves an instance of the appropriate type. The returned object may or
     * may not be a new instance, depending on the implementation.
     *
     * @return an instance of the appropriate type
     */
    @Override
    public MobilePhone get() {
        return new MobilePhone("iPhone5s", Color.WHITE, OS.IOS);
    }
}

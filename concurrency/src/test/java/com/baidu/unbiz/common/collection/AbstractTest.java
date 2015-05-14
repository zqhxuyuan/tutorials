/**
 * 
 */
package com.baidu.unbiz.common.collection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年8月1日 下午1:36:11
 */
public abstract class AbstractTest {
    protected final boolean isEqual(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    protected final <T> T cloneBySerialization(T obj) {
        if (obj == null || obj instanceof Serializable) {
            try {
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(byteStream);

                oos.writeObject(obj);

                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(byteStream.toByteArray()));

                @SuppressWarnings("unchecked")
                T copy = (T) ois.readObject();

                return copy;
            } catch (ClassNotFoundException cnfe) {
                throw new RuntimeException("Failed deep cloning object", cnfe);
            } catch (IOException ioe) {
                throw new RuntimeException("Failed deep cloning object", ioe);
            }
        } else {
            throw new UnsupportedOperationException("Object is not serializable");
        }
    }
}

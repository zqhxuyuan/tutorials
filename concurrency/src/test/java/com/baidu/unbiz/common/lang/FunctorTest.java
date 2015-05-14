/**
 * 
 */
package com.baidu.unbiz.common.lang;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.baidu.unbiz.common.Emptys;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月21日 下午7:20:26
 */
public class FunctorTest {

    @Test
    public void functor_no_parameter() {
        Functor functor = new Functor("functor", "length");
        functor.execute(Emptys.EMPTY_OBJECT_ARRAY);

        assertEquals(7, functor.getResult());
    }

    @Test
    public void functor_parameter() {
        Functor functor = new Functor(new StringBuilder("functor\n"), "append", char[].class);
        functor.execute(new char[] { 'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd' });

        assertEquals("functor\nHello World", functor.getResult().toString());
    }

}

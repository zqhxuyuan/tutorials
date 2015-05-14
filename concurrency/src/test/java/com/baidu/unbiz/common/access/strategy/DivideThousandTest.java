/**
 * 
 */
package com.baidu.unbiz.common.access.strategy;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import com.baidu.unbiz.common.access.AccessStrategy;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年8月1日 上午3:25:27
 */
public class DivideThousandTest {

    @Test
    public void find() {
        AccessStrategy strategy = new DivideThousand();
        String where = strategy.find(1234567890);
        assertEquals("1\\1234\\1234567\\1234567890".replace("\\", File.separator), where);

        where = strategy.find(123456789);
        assertEquals("123\\123456\\123456789".replace("\\", File.separator), where);
    }

}

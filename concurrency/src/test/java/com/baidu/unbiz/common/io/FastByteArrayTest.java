package com.baidu.unbiz.common.io;

import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年8月1日 下午1:50:02
 */
public class FastByteArrayTest {

    @Test
    public void testFbat() throws IOException {
        FastByteArrayOutputStream fbaos = new FastByteArrayOutputStream();

        fbaos.write(173);
        fbaos.write(new byte[] { 1, 2, 3 });
        fbaos.write(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }, 4, 3);

        byte[] result = fbaos.toByteArray();
        StreamUtil.close(fbaos);
        byte[] expected = new byte[] { (byte) 173, 1, 2, 3, 5, 6, 7 };

        assertTrue(Arrays.equals(expected, result));

        fbaos = new FastByteArrayOutputStream(2);

        fbaos.write(173);
        fbaos.write(new byte[] { 1, 2, 3 });
        fbaos.write(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }, 4, 3);

        result = fbaos.toByteArray();
        StreamUtil.close(fbaos);
        expected = new byte[] { (byte) 173, 1, 2, 3, 5, 6, 7 };

        assertTrue(Arrays.equals(expected, result));
    }

    @Test
    public void testFbatSingle() throws IOException {
        FastByteArrayOutputStream fbaos = new FastByteArrayOutputStream(2);

        fbaos.write(73);
        fbaos.write(74);
        fbaos.write(75);
        fbaos.write(76);
        fbaos.write(77);

        byte[] result = fbaos.toByteArray();
        StreamUtil.close(fbaos);
        byte[] expected = new byte[] { 73, 74, 75, 76, 77 };

        assertTrue(Arrays.equals(expected, result));
    }
}

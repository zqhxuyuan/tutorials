/**
 *
 */
package com.baidu.unbiz.common.able;

/**
 * FIXME Object?
 *
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月22日 上午11:50:11
 */
public interface ArrayConvertable {

    boolean[] convert2Boolean(Object value);

    byte[] convert2Byte(Object value);

    char[] convert2Char(Object value);

    double[] convert2Double(Object value);

    float[] convert2Float(Object value);

    int[] convert2Int(Object value);

    long[] convert2Long(Object value);

    short[] convert2Short(Object value);

    String[] convert2String(Object value);

}

/**
 * 
 */
package com.baidu.unbiz.common;

import java.util.Random;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月15日 下午3:10:04
 */
public abstract class RandomUtil {

    private final static Random random = new Random();

    /**
     * 获取begin和end之间的整数 [begin,end]
     * 
     * @param begin 起始值
     * @param end 终止值
     * @return begin和end之间的整数
     */
    public static int next(int begin, int end) {
        if (end <= begin) {
            throw new IllegalArgumentException("end must larger than begin");
        }

        int minus = random.nextInt(end - begin + 1);
        return (begin + minus);
    }

    /**
     * 获取begin和end之间的整数 [begin,end]
     * 
     * @param begin 起始值
     * @param end 终止值
     * @return begin和end之间的整数
     */
    public static long next(long begin, long end) {
        if (end <= begin) {
            throw new IllegalArgumentException("end must larger than begin");
        }

        long minus = random.nextInt((int) (end - begin + 1));
        return (begin + minus);
    }

    /**
     * 获取pSngBegin和pSngEnd之间的数值 [pSngBegin,pSngEnd)
     * 
     * @param pSngBegin 起始值
     * @param pSngEnd 终止值
     * @return pSngBegin和pSngEnd之间的数值
     */
    public static double getRandomNum(double pSngBegin, double pSngEnd) {
        if (pSngEnd <= pSngBegin) {
            throw new IllegalArgumentException("pSngEnd must larger than pSngBegin");
        }

        return (pSngEnd - pSngBegin) * Math.random() + pSngBegin;
    }

    /**
     * 按照一定概率进行随机<br>
     * 该方法参数太多，不做合法检测<br>
     * FIXME
     * 
     * @param pSngBegin 随机数范围的开始数字
     * @param pSngEnd 随机数范围结束数字
     * @param pSngPB 要随机的数字的开始数字
     * @param pSngPE 要随机的数字的结束数字
     * @param pBytP 要随机的数字随机概率
     * @return 按照一定概率随机的数字
     */
    public static double getRndNumP(double pSngBegin, double pSngEnd, double pSngPB, double pSngPE, double pBytP) {
        double sngPLen = pSngPE - pSngPB;
        // total length
        double sngTLen = pSngEnd - pSngBegin;
        // FIXME may throw java.lang.ArithmeticException : / by zero
        if ((sngPLen / sngTLen) * 100 == pBytP) {
            return getRandomNum(pSngBegin, pSngEnd);
        }

        // ((sngPLen + sngIncreased) / (sngTLen + sngIncreased)) * 100 =
        // bytP
        double sngIncreased = ((pBytP / 100) * sngTLen - sngPLen) / (1 - (pBytP / 100));
        // 缩放回原来区间
        double sngResult = getRandomNum(pSngBegin, pSngEnd + sngIncreased);
        if (pSngBegin <= sngResult && sngResult <= pSngPB) {
            return sngResult;
        }

        if (pSngPB <= sngResult && sngResult <= (pSngPE + sngIncreased)) {
            return pSngPB + (sngResult - pSngPB) * sngPLen / (sngPLen + sngIncreased);
        }

        if ((pSngPE + sngIncreased) <= sngResult && sngResult <= (pSngEnd + sngIncreased)) {
            return sngResult - sngIncreased;
        }

        return 0d;
    }

}

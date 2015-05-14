/**
 * 
 */
package com.baidu.unbiz.common.division;

import static org.junit.Assert.assertEquals;
import junit.framework.AssertionFailedError;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月17日 上午2:23:11
 */
public class DivisionManagerTest {

    private static final int THREAD_COUNT = 1;
    private static final int LOOPS = 1;

    DivisionManager divisionManager;

    @Before
    public void setUp() throws Exception {
        divisionManager = ChinaDivisionManager.getInstance();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getDivisionById() {
        assertEquals(divisionManager.getDivisionById(120106).getDivisionName(), "红桥区");
    }

    @Test
    public void getDivisionByName() {
        assertEquals(divisionManager.getDivisionByName("红桥区").get(0).getDivisionName(), "红桥区");
    }

    @Test
    public void getDivisionByAbbName() {
        assertEquals(divisionManager.getDivisionByAbbName("内蒙古").get(0).getDivisionName(), "内蒙古自治区");
    }

    @Test
    public void getDivisionByTName() {
        assertEquals(((ChinaDivision) divisionManager.getDivisionByTName("澳門特別行政區").get(0)).getDivisionTname(),
                "澳門特別行政區");
    }

    @Test
    public void getDivisionByZip() {
        assertEquals(divisionManager.getDivisionByZip("300131").get(0).getDivisionName(), "红桥区");
    }

    @Test
    public void getObscureDivisionByTName() {
        for (int i = 0; i < LOOPS; i++) {
            assertEquals(divisionManager.getObscureDivisionByTName("澳门").size(), 2);
        }

    }

    @Test
    public void getObscureDivisionByName() {
        assertEquals(divisionManager.getObscureDivisionByName("澳门").size(), 2);
    }

    @Test
    public void getProvDisivion() {
        assertEquals(divisionManager.getProvinceDisivion().size(), 35);
    }

    @Test
    public void isProvinceDivision() {
        assertEquals(divisionManager.isProvinceDivision(140000), true);
        assertEquals(divisionManager.isProvinceDivision(653227), false);
    }

    @Test
    public void isProvinceNameDivision() {
        assertEquals(divisionManager.isProvinceNameDivision("山西省"), true);
        assertEquals(divisionManager.isProvinceNameDivision("杭州市"), false);
    }

    @Test
    public void isCityDivision() {
        assertEquals(divisionManager.isCityDivision(140100), true);
        assertEquals(divisionManager.isCityDivision(140105), false);
    }

    @Test
    public void isCityNameDivision() {
        assertEquals(divisionManager.isCityNameDivision("太原市"), true);
        assertEquals(divisionManager.isCityNameDivision("浙江省"), false);
    }

    @Test
    public void isRegionDivision() {
        assertEquals(divisionManager.isRegionDivision(140221), true);
        assertEquals(divisionManager.isRegionDivision(653200), false);
    }

    @Test
    public void isRegionNameDivision() {
        assertEquals(divisionManager.isRegionNameDivision("天镇县"), true);
        assertEquals(divisionManager.isRegionNameDivision("台北市"), false);
    }

    @Test
    public void testPerformance() throws InterruptedException {
        ThreadTest[] runnables = new ThreadTest[THREAD_COUNT];
        Thread[] threads = new Thread[THREAD_COUNT];

        for (int i = 0; i < threads.length; i++) {
            runnables[i] = new ThreadTest();
            threads[i] = new Thread(runnables[i], "thread_" + i);
        }

        long start = System.currentTimeMillis();

        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }

        long duration = System.currentTimeMillis() - start;

        System.out.println("总耗费时间：" + ((duration)));

        int failCount = 0;

        for (int i = 0; i < threads.length; i++) {
            failCount += runnables[i].failCount;
        }

        System.out.println("总失败次数: " + failCount + " of " + (LOOPS * THREAD_COUNT));
    }

    private class ThreadTest implements Runnable {
        private int failCount = 0;

        public void run() {
            try {
                getObscureDivisionByName();
            } catch (AssertionFailedError e) {
                System.out.println(Thread.currentThread().getName() + ": " + e.getMessage());
                failCount++;
            }
        }
    }

}

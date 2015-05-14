/**
 * 
 */
package com.baidu.unbiz.common.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.baidu.unbiz.common.logger.CachedLogger;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月31日 下午7:36:16
 */
public class MoneyTest extends CachedLogger {

    private Money money;

    @Before
    public void setUp() throws Exception {
        // 十元十分
        money = new Money(10, 10);
    }

    @After
    public void tearDown() throws Exception {
        money = null;
    }

    @Test
    public void getCent() {
        assertEquals(10 * 100 + 10, money.getCent());
    }

    @Test
    public void getCurrencyCode() {
        assertEquals("CNY", money.getCurrencyCode());
    }

    @Test
    public void getCentFactor() {
        // 元与分兑换
        assertEquals(100, money.getCentFactor());
    }

    @Test
    public void greaterThan() {
        assertTrue(money.greaterThan(new Money("9.85")));
        assertFalse(money.greaterThan(new Money("11")));

        assertEquals(money, new Money(10.1));
        assertEquals(money, new Money("10.1"));
    }

    @Test
    public void add() {
        Money newMoney = money.add(new Money("2.5"));
        assertEquals(1010 + 250, newMoney.getCent());
        newMoney = money.add(new Money("-2.5"));
        assertEquals(1010 - 250, newMoney.getCent());
    }

    @Test
    public void addTo() {
        money.addTo(new Money("2.5"));
        assertEquals(1010 + 250, money.getCent());
        money.addTo(new Money("-2.5"));
        assertEquals(1010, money.getCent());
    }

    @Test
    public void subtract() {
        Money newMoney = money.subtract(new Money("2.5"));
        assertEquals(1010 - 250, newMoney.getCent());
        newMoney = money.subtract(new Money("-2.5"));
        assertEquals(1010 + 250, newMoney.getCent());
    }

    @Test
    public void subtractFrom() {
        money.subtractFrom(new Money("2.5"));
        assertEquals(1010 - 250, money.getCent());
        money.subtractFrom(new Money("-2.5"));
        assertEquals(1010, money.getCent());
    }

    @Test
    public void multiply() {
        Money newMoney = money.multiply(0.5);
        assertEquals(505, newMoney.getCent());
        newMoney = money.multiply(2);
        assertEquals(2020, newMoney.getCent());
    }

    @Test
    public void multiplyBy() {
        money.multiplyBy(0.5);
        assertEquals(505, money.getCent());
        money.multiplyBy(2);
        assertEquals(1010, money.getCent());
    }

    @Test
    public void divide() {
        Money newMoney = money.divide(0.5);
        assertEquals(2020, newMoney.getCent());
        newMoney = money.divide(2);
        assertEquals(505, newMoney.getCent());
    }

    @Test
    public void divideBy() {
        money.divideBy(0.5);
        assertEquals(2020, money.getCent());
        money.divideBy(2);
        assertEquals(1010, money.getCent());
    }

    @Test
    public void allocate() {
        for (Money mon : money.allocate(10)) {
            assertEquals(101, mon.getCent());
        }
        Money xMoney = new Money();
        for (Money mon : money.allocate(new long[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 })) {
            xMoney.addTo(mon);
        }
        assertEquals(money, xMoney);
        // Arrays.toString(money.allocate(10));
    }

    @Test
    public void dump() {
        logger.info(money.dump());
    }

    @Test
    public void getDisplayUnit() {
        assertEquals("元", money.getDisplayUnit());
    }
}

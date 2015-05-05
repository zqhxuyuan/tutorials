package com.baidu.unbiz.common.date;

import java.io.Serializable;
import java.math.BigDecimal;

import com.baidu.unbiz.common.HashCode;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月22日 下午5:16:16
 */
public class JulianDateStamp implements Serializable, Cloneable {

    /**
	 * 
	 */
    private static final long serialVersionUID = -7385023985876146308L;

    protected int integer;

    public int getInteger() {
        return integer;
    }

    protected double fraction;

    public double getFraction() {
        return fraction;
    }

    public int getJulianDayNumber() {
        if (fraction >= 0.5) {
            return integer + 1;
        }
        return integer;
    }

    public JulianDateStamp() {
    }

    public JulianDateStamp(double jd) {
        set(jd);
    }

    public JulianDateStamp(int i, double f) {
        set(i, f);
    }

    public JulianDateStamp(BigDecimal bd) {
        double d = bd.doubleValue();
        integer = (int) d;
        bd = bd.subtract(new BigDecimal(integer));
        fraction = bd.doubleValue();
    }

    public double doubleValue() {
        return (double) integer + fraction;
    }

    public BigDecimal toBigDecimal() {
        BigDecimal bd = new BigDecimal(integer);
        return bd.add(new BigDecimal(fraction));
    }

    @Override
    public String toString() {
        String s = Double.toString(fraction);
        int i = s.indexOf('.');
        s = s.substring(i);
        return integer + s;
    }

    public JulianDateStamp add(JulianDateStamp jds) {
        int i = this.integer + jds.integer;
        double f = this.fraction + jds.fraction;
        set(i, f);
        return this;
    }

    public JulianDateStamp add(double delta) {
        set(this.integer, this.fraction + delta);
        return this;
    }

    public JulianDateStamp sub(JulianDateStamp jds) {
        int i = this.integer - jds.integer;
        double f = this.fraction - jds.fraction;
        set(i, f);
        return this;
    }

    public JulianDateStamp sub(double delta) {
        set(this.integer, this.fraction - delta);
        return this;
    }

    public void set(int i, double f) {
        integer = i;
        int fi = (int) f;
        f -= fi;
        integer += fi;
        if (f < 0) {
            f += 1;
            integer--;
        }
        this.fraction = f;
    }

    public void set(double jd) {
        integer = (int) jd;
        fraction = jd - (double) integer;
    }

    public int daysBetween(JulianDateStamp otherDate) {
        int difference = daysSpan(otherDate);
        return difference >= 0 ? difference : -difference;
    }

    public int daysSpan(JulianDateStamp otherDate) {
        int now = getJulianDayNumber();
        int then = otherDate.getJulianDayNumber();
        return now - then;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof JulianDateStamp)) {
            return false;
        }
        JulianDateStamp stamp = (JulianDateStamp) object;
        return (stamp.integer == this.integer) && (Double.compare(stamp.fraction, this.fraction) == 0);
    }

    @Override
    public int hashCode() {
        int result = HashCode.SEED;
        result = HashCode.hash(result, integer);
        result = HashCode.hash(result, fraction);
        return result;
    }

    @Override
    protected JulianDateStamp clone() {
        return new JulianDateStamp(this.integer, this.fraction);
    }

    public JulianDateStamp getReducedJulianDate() {
        return new JulianDateStamp(integer - 2400000, fraction);
    }

    public void setReducedJulianDate(double rjd) {
        set(rjd + 2400000);
    }

    public JulianDateStamp getModifiedJulianDate() {
        return new JulianDateStamp(integer - 2400000, fraction - 0.5);
    }

    public void setModifiedJulianDate(double mjd) {
        set(mjd + 2400000.5);
    }

    public JulianDateStamp getTruncatedJulianDate() {
        return new JulianDateStamp(integer - 2440000, fraction - 0.5);
    }

    public void setTruncatedJulianDate(double tjd) {
        set(tjd + 2440000.5);
    }
}

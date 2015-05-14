/**
 * 
 */
package com.baidu.unbiz.common.sample;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月31日 下午3:54:31
 */
public class AnnotationClass {

    private int x;
    @Test(value = "y")
    private int y;

    private String z;
    @Test(value = "d")
    private Date d;

    public int getX() {
        return x;
    }

    @Test(value = "setX")
    public void setX(int x) {
        this.x = x;
    }

    @Test(value = "getY")
    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Test(value = "getZ")
    public String getZ() {
        return z;
    }

    @Test(value = "setZ")
    public void setZ(String z) {
        this.z = z;
    }

    @Test(value = "getD")
    public Date getD() {
        return d;
    }

    @Test(value = "setD")
    public void setD(Date d) {
        this.d = d;
    }

    @Test(value = "toString")
    public String toString() {
        return super.toString();
    }

    @Test(value = "clone")
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Test {
        String value();
    }

}

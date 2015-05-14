/**
 * 
 */
package com.baidu.unbiz.common.bean;

import org.junit.Assert;
import org.junit.Test;

import com.baidu.unbiz.common.bean.sample.Abean;
import com.baidu.unbiz.common.bean.sample.AnnoBean;
import com.baidu.unbiz.common.bean.sample.Cbean;
import com.baidu.unbiz.common.logger.CachedLogger;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月19日 上午2:09:46
 */
public class BeanCopyTest extends CachedLogger {

    @Test
    public void copyProperties() {
        Abean bean = new Abean();
        bean.setFooProp("ggg");
        Abean acopied = new Abean();
        BeanCopy.copyProperties(bean, acopied);
        Assert.assertEquals("ggg", acopied.getFooProp());

        Cbean src = new Cbean();
        src.setS2("11111");
        src.setS3("aaaaa");
        Cbean dest = new Cbean();
        BeanCopy.copyProperties(src, dest);
        Assert.assertEquals("aaaaa", dest.getS3());
        logger.info(dest);
    }

    @Test
    public void copyByMethod() {
        Abean bean = new Abean();
        bean.setFooProp("ggg");
        Abean acopied = new Abean();
        BeanCopy.copyByMethod(bean, acopied);
        Assert.assertEquals("ggg", acopied.getFooProp());

        Cbean src = new Cbean();
        src.setS2("11111");
        src.setS3("aaaaa");
        Cbean dest = new Cbean();
        BeanCopy.copyByMethod(src, dest);
        Assert.assertEquals("aaaaa", dest.getS3());
        logger.info(dest);

    }

    @Test
    public void copyByAnnotation() {
        AnnoBean bean = new AnnoBean();
        bean.setCompany(new StringBuilder("comp"));
        bean.setTest1(11);
        bean.setTest2(22);
        bean.setTest3("33");
        bean.setTest4("44");
        AnnoBean acopied = new AnnoBean();
        BeanCopy.copyByAnnotation(bean, acopied);
        Assert.assertNull(null, acopied.getTest3());
        Assert.assertEquals("44", acopied.getTest4());

        logger.info(acopied);
    }

}

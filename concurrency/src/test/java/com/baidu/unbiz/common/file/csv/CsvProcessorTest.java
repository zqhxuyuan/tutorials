/**
 * 
 */
package com.baidu.unbiz.common.file.csv;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.baidu.unbiz.common.ClassLoaderUtil;
import com.baidu.unbiz.common.CollectionUtil;
import com.baidu.unbiz.common.file.FileProcessor;
import com.baidu.unbiz.common.file.FileType;
import com.baidu.unbiz.common.logger.CachedLogger;
import com.baidu.unbiz.common.sample.CsvBean;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年8月1日 下午2:16:00
 */
public class CsvProcessorTest extends CachedLogger {

    private static final String testBeanPath = ClassLoaderUtil.getClasspath() + File.separator + "test_bean.csv";

    private static final String testBeansPath = ClassLoaderUtil.getClasspath() + File.separator + "test_beans.csv";

    private FileProcessor processor;

    @Before
    public void setUp() throws Exception {
        processor = FileType.CSV.createProcessor();
    }

    @After
    public void tearDown() throws Exception {
        processor = null;
    }

    @Test
    public void importBean() {
        CsvBean bean = createBean("小明", 25, "自由", "某某小区");

        try {
            processor.importBean(bean, testBeanPath);
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    // null会转变成""
    @Test
    public void importBeans() {
        List<CsvBean> list = CollectionUtil.createArrayList();
        CsvBean bean = createBean("小明", 25, "自由", "某某小区");
        list.add(bean);
        bean = createBean("小李", 28, "IT", "某某家园");
        list.add(bean);
        bean = createBean("小红", 7, null, "父母家");
        list.add(bean);
        bean = createBean("小王", 26, "金融", "某某别墅");
        list.add(bean);

        try {
            processor.importBeans(bean.getClass(), list.toArray(new CsvBean[0]), testBeansPath);
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    @Test
    public void exportBean() throws IOException {

        importBean();

        CsvBean bean = processor.exportBean(CsvBean.class, testBeanPath);

        assertEquals(bean, createBean("小明", 25, "自由", "某某小区"));
    }

    @Test
    public void exportBeans() throws IOException {

        importBeans();

        List<CsvBean> list = processor.exportBeans(CsvBean.class, testBeansPath);

        for (CsvBean bean : list) {
            logger.info(bean);
        }
    }

    private CsvBean createBean(String name, int age, String work, String home) {
        CsvBean bean = new CsvBean();
        bean.setName(name);
        bean.setAge(age);
        bean.setWork(work);
        bean.setHome(home);
        return bean;
    }
}

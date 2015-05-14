/**
 * 
 */
package com.baidu.unbiz.common.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.baidu.unbiz.common.ObjectUtil;
import com.baidu.unbiz.common.SystemUtil;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月31日 下午8:31:53
 */
public class AppInfoTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void setLocation() {// org.slf4j
        AppInfo app = new AppInfo();

        app.setLocation("org.slf4j");
        assertEquals("1.7.7", app.getAppVersion());

        // app.setLocation("java.lang");
        // assertEquals("Java Platform API Specification", app.getSpecificationTitle());
        // assertEquals("1.6", app.getSpecificationVersion());
        // assertEquals("Sun Microsystems, Inc.", app.getSpecificationVendor());
        // assertEquals("Java Runtime Environment", app.getImplementationTitle());
    }

    @Test
    public void executeCommand() {
        AppInfo app = new AppInfo();
        if (SystemUtil.getOsInfo().isWindows()) {
            app.setProcessable(new ProcessClosure());
            String cmd = "ipconfig";
            String result = app.executeCommand(cmd);
            assertTrue(ObjectUtil.isNotEmpty(result));
            return;

        }
        app.setProcessable(new ShellClosure());
        String cmd = "/sbin/ifconfig";
        String result = app.executeCommand(cmd);
        assertTrue(ObjectUtil.isNotEmpty(result));
    }

    @Test
    public void executeCommands() {
        AppInfo app = new AppInfo();
        if (SystemUtil.getOsInfo().isWindows()) {
            app.setProcessable(new ProcessClosure());
            String cmd = "ipconfig,java";
            String result = app.executeCommands(cmd);
            assertTrue(ObjectUtil.isNotEmpty(result));
            return;

        }
        app.setProcessable(new ShellClosure());
        String cmd = "/sbin/ifconfig,java";
        String result = app.executeCommands(cmd);
        assertTrue(ObjectUtil.isNotEmpty(result));

    }

}

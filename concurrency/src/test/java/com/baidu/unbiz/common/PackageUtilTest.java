/**
 * 
 */
package com.baidu.unbiz.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.baidu.unbiz.common.CollectionUtil;
import com.baidu.unbiz.common.PackageUtil;
import com.baidu.unbiz.common.logger.CachedLogger;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月17日 下午4:22:00
 */
public class PackageUtilTest extends CachedLogger {

    @Test
    public void getClassesInPackage() throws IOException {
        // null
        assertNull(PackageUtil.getClassesInPackage(null));
        assertNull(PackageUtil.getClassesInPackage(""));
        assertNull(PackageUtil.getClassesInPackage("     "));

        assertNull(PackageUtil.getClassesInPackage(null, null, null));
        assertNull(PackageUtil.getClassesInPackage("", null, null));
        assertNull(PackageUtil.getClassesInPackage("     ", null, null));
        assertNull(PackageUtil
                .getClassesInPackage(null, Arrays.asList(new String[] { "com.baidu.unbiz.common" }), null));
        assertNull(PackageUtil.getClassesInPackage("", Arrays.asList(new String[] { "com.baidu.unbiz.common" }), null));
        assertNull(PackageUtil.getClassesInPackage("     ", Arrays.asList(new String[] { "com.baidu.unbiz.common" }),
                null));

        assertTrue(CollectionUtil.isEmpty(PackageUtil.getClassesInPackage("com.baidu.beidou.xxx")));
        assertTrue(CollectionUtil.isEmpty(PackageUtil.getClassesInPackage("notexist")));

        List<String> utils = PackageUtil.getClassesInPackage("com.baidu.unbiz.common");
        assertTrue(CollectionUtil.isNotEmpty(utils));

        List<String> subUtils = PackageUtil.getClassesInPackage("com.baidu.unbiz.common.*");
        assertTrue(CollectionUtil.isNotEmpty(subUtils));
        assertTrue(CollectionUtil.isNotEmpty(utils));
        assertTrue(subUtils.size() > utils.size());
        assertTrue(subUtils.containsAll(utils));

        assertEquals(PackageUtil.getClassesInPackage("com.baidu.unbiz.common.*"),
                PackageUtil.getClassesInPackage("com.baidu.unbiz.common.*", null, null));
        assertEquals(PackageUtil.getClassesInPackage("com.baidu.unbiz.common.*"),
                PackageUtil.getClassesInPackage("com.baidu.unbiz.common.*", Arrays.asList(new String[] { ".*" }), null));

        assertTrue(CollectionUtil.isEmpty(PackageUtil.getClassesInPackage("com.baidu.unbiz.common.*",
                Arrays.asList(new String[] { ".*" }), Arrays.asList(new String[] { ".*" }))));
        assertTrue(CollectionUtil.isEmpty(PackageUtil.getClassesInPackage("com.baidu.unbiz.common.*", null,
                Arrays.asList(new String[] { ".*" }))));

        List<String> includeTests =
                PackageUtil.getClassesInPackage("com.baidu.unbiz.common.*", Arrays.asList(new String[] { ".*Test" }),
                        null);
        assertTrue(CollectionUtil.isNotEmpty(includeTests));
        List<String> excludeTests =
                PackageUtil.getClassesInPackage("com.baidu.unbiz.common.*", null,
                        Arrays.asList(new String[] { ".*Test" }));
        assertTrue(CollectionUtil.isNotEmpty(excludeTests));
        assertEquals(subUtils.size(), includeTests.size() + excludeTests.size());

        List<String> langs = PackageUtil.getClassesInPackage("org.slf4j");
        assertTrue(CollectionUtil.isNotEmpty(langs));
        List<String> subLangs = PackageUtil.getClassesInPackage("org.slf4j.*");
        assertTrue(CollectionUtil.isNotEmpty(subLangs));
        // FIXME
        assertEquals(subLangs, langs);
    }

    @Test
    public void getResourceInPackage() throws IOException {
        // null
        assertNull(PackageUtil.getResourceInPackage(null));
        assertNull(PackageUtil.getResourceInPackage(""));
        assertNull(PackageUtil.getResourceInPackage("     "));

        assertTrue(CollectionUtil.isEmpty(PackageUtil.getResourceInPackage("com.baidu.beidou.xxx")));
        assertTrue(CollectionUtil.isEmpty(PackageUtil.getResourceInPackage("notexist")));
        List<String> utils = PackageUtil.getResourceInPackage("com.baidu.unbiz.common");
        assertTrue(CollectionUtil.isNotEmpty(utils));
        List<String> subUtils = PackageUtil.getResourceInPackage("com.baidu.unbiz.common.*");
        assertTrue(CollectionUtil.isNotEmpty(subUtils));
        assertEquals(utils, subUtils);

        List<String> langs = PackageUtil.getResourceInPackage("org.slf4j");
        assertTrue(CollectionUtil.isNotEmpty(langs));
        List<String> subLangs = PackageUtil.getResourceInPackage("org.slf4j.*");
        assertTrue(CollectionUtil.isNotEmpty(subLangs));
        // FIXME
        assertEquals(subLangs, langs);

    }

    @Test
    public void getPackage() {
        assertNull(PackageUtil.getPackage(null));

        assertEquals("java.lang", PackageUtil.getPackage(String.class).getName());

        assertEquals("com.baidu.unbiz.common", PackageUtil.getPackage(PackageUtil.class).getName());

    }
}

/**
 * 
 */
package com.github.NoahShen.jue.util;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author noah
 *
 */
public class ConcurrentLRUCacheTest {
	
	private ConcurrentLRUCache<String, String> cache;
	
	@Before
	public void setUp() throws Exception {
		cache = new ConcurrentLRUCache<String, String>(10);
	}

	@After
	public void tearDown() throws Exception {
		cache = null;
	}
	
	@Test
	public void testPut() {
		for (int i = 0; i < 10; ++i) {
			String key = i + "";
			String value = i + "";
			cache.put(key, value);
		}
		for (int i = 0; i < 10; ++i) {
			String key = i + "";
			String value = i + "";
			Assert.assertEquals(value, cache.get(key));
		}
		Assert.assertEquals(10, cache.size());
	}
}

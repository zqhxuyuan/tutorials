/**
 * 
 */
package com.baidu.unbiz.common.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.baidu.unbiz.common.CollectionUtil;
import com.baidu.unbiz.common.test.TestUtil;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月31日 下午7:36:34
 */
public class UUIDTest {

    private final int loop = 5000;
    private final int concurrency = 20;
    private UUID uuid;
    private String instanceId;

    @Before
    public final void init() throws Exception {
        newUUID(null);
    }

    private void newUUID(Boolean noCase) {
        if (uuid == null) {
            uuid = new UUID();
        } else {
            uuid = new UUID(noCase);
        }

        instanceId = TestUtil.getFieldValue(uuid, "instanceId", String.class);
        assertNotNull(instanceId);
    }

    @Test
    public void generate() {
        newUUID(false);

        String sid = uuid.nextID();

        assertTrue(sid.length() > instanceId.length());
        assertTrue(sid.startsWith(instanceId));

        assertTrue(sid.matches("[A-Za-z0-9-]+"));
    }

    @Test
    public void generate_noCase() {
        newUUID(true);

        String sid = uuid.nextID();

        assertTrue(sid.length() > instanceId.length());
        assertTrue(sid.startsWith(instanceId));

        assertTrue(sid.matches("[A-Z0-9-]+"));
    }

    @Test
    public synchronized void performance() throws InterruptedException {
        final String[][] results = new String[concurrency][];
        Thread[] threads = new Thread[concurrency];

        for (int i = 0; i < concurrency; i++) {
            final String[] result = new String[loop];
            results[i] = result;
            threads[i] = new Thread(new Runnable() {
                public void run() {
                    for (int i = 0; i < loop; i++) {
                        result[i] = uuid.nextID();
                    }
                }
            }, "t-" + (i + 1));
        }

        long start = System.currentTimeMillis();

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        long duration = System.currentTimeMillis() - start;

        System.out.printf("%s: requests=%d, concurrency=%d%n", uuid.getClass().getSimpleName(), concurrency * loop,
                concurrency);

        System.out.printf("  Total time: %,d ms.%n", duration);
        System.out.printf("Average time: %,2.2f \u03BCs.%n", (double) duration / concurrency / loop * 1000);

        // 检查重复
        Set<String> allIDs = CollectionUtil.createHashSet();

        for (String[] result : results) {
            for (String id : result) {
                assertNotNull(id);
                assertTrue(id, !id.contains("+") && !id.contains("/") && !id.contains("="));
                allIDs.add(id);
            }
        }

        assertEquals(concurrency * loop, allIDs.size());
    }

}

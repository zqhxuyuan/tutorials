/**
 * 
 */
package com.github.NoahShen.jue;

import org.junit.Assert;
import org.junit.Test;

import com.github.NoahShen.jue.doc.DocObject;
import com.github.NoahShen.jue.file.AODataSyncFile;

/**
 * @author noah
 *
 */
public class JueTest {

	private String testFile = "/tmp/jueTestFile.jue";
	
	private Jue jue;
	
	@Test
	public void testJue() {
		FileConfig config = new FileConfig();
		config.setKeyTreeMin(10);
		config.setValueRevTreeMin(10);
		config.setValueCompressed(true);
		config.setCompressionType(FileConfig.ZLIB);
		config.setCacheCapacity(10);
		config.setDataBufferSize(AODataSyncFile.DEFAULT_MAX_DATA_BUFFER_SIZE);
		jue = new Jue(testFile, config);
		
		//put
		int propertycount = 10;
		DocObject docObj = new DocObject();
		for (int i = 0; i < propertycount; i++) {
			docObj.put("property" + i, true);
		}
		jue.put("key", docObj, -1, true);
		
		//get
		DocObject obj = jue.get("key", -1);
		Assert.assertEquals(true, obj.getBoolean("property1"));
		System.out.println(obj);
		
		//merge
		DocObject docObjMerge = new DocObject();
		docObjMerge.put("property" + propertycount, true);
		jue.put("key", docObjMerge, -1, true);
		
		DocObject obj2 = jue.get("key", -1);
		Assert.assertEquals(true, obj2.getBoolean("property1"));
		Assert.assertEquals(true, obj2.getBoolean("property2"));
		System.out.println(obj2);
		
		//not merge
		DocObject docObjNotMerge = new DocObject();
		docObjNotMerge.put("property" + propertycount, true);
		jue.put("key", docObjNotMerge, -1, false);
		
		DocObject obj3 = jue.get("key", -1);
		Assert.assertEquals(false, obj3.has("property1"));
		Assert.assertEquals(false, obj3.has("property2"));
		Assert.assertEquals(true, obj3.getBoolean("property" + propertycount));
		System.out.println(obj3);
		
		
		//compact
		jue.compact(-1);
		
		jue.close();
	}

}

package com.github.NoahShen.jue.doc;

import org.junit.Assert;
import org.junit.Test;

public class DocTest {

	@Test
	public void testDocObject() throws DocException {
		String json = "{\"b\":2,\"a\":1}";
		String arrayJson = "[{a:1},{b:2}]";
		DocArray docArray = new DocArray(arrayJson);
		Assert.assertEquals(1, docArray.getDocObject(0).getInt("a"));

		DocObject docObj = new DocObject(json);
		docObj.put("array", docArray);
		Assert.assertEquals(1, docObj.getInt("a"));

		System.out.println(docArray.toString());
		System.out.println(docObj.toString());
	}

}

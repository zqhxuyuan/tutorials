package com.zqh.midd.netty.GameDispatcher.util;

import com.google.gson.Gson;

public class GsonUtil {
	
	/**
	 * 单纯的new，可进行进一步优化
	 * @return
	 */
	public static Gson getGson() {
		return new Gson();
	}
	
}

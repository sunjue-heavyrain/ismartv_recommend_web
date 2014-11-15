package com.ismartv.recommend.cache.cacheclient;

import com.ismartv.recommend.cache.CacheClient;
import com.ismartv.recommend.cache.memory.MemoryCache;

public class MemoryCacheClient implements CacheClient {

	private MemoryCache<String, String> memoryCache = new MemoryCache<String, String>(
			10000);

	@Override
	public String get(String region, String key) {
		return memoryCache.getElement(region + "_" + key);
	}

	@Override
	public boolean set(String region, String key, String value) {
		memoryCache.addElement(region + "_" + key, value);
		return true;
	}

	@Override
	public boolean isInMemory() {
		return true;
	}

}

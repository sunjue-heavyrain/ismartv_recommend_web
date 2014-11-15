package com.ismartv.recommend.cache;

public interface CacheClient {

	public String get(String region, String key);

	public boolean set(String region, String key, String value);

	public boolean isInMemory();
}

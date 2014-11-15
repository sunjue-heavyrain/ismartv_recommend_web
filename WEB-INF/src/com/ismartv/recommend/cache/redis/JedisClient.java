package com.ismartv.recommend.cache.redis;

public interface JedisClient {
	public String get(String region, String key);

	public boolean set(String region, String key, String value);
}

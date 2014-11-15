package com.ismartv.recommend.cache.cacheclient;

import com.ismartv.recommend.cache.CacheClient;
import com.ismartv.recommend.cache.redis.JedisClient;
import com.ismartv.recommend.cache.redis.JedisPoolClient;

public class RedisCacheClient implements CacheClient {

	private JedisClient jedisClient;

	public RedisCacheClient() {
		jedisClient = new JedisPoolClient();
	}

	@Override
	public String get(String region, String key) {
		String result = jedisClient.get(region, key);
		return result;
	}

	@Override
	public boolean set(String region, String key, String value) {
		return jedisClient.set(region, key, value);
	}

	/**
	 * @return the pool
	 */
	public JedisClient getPool() {
		return jedisClient;
	}

	/**
	 * @param pool
	 *            the pool to set
	 */
	public void setPool(JedisClient pool) {
		this.jedisClient = pool;
	}

	/**
	 * @return the jedisClient
	 */
	public JedisClient getJedisClient() {
		return jedisClient;
	}

	/**
	 * @param jedisClient
	 *            the jedisClient to set
	 */
	public void setJedisClient(JedisClient jedisClient) {
		this.jedisClient = jedisClient;
	}

	@Override
	public boolean isInMemory() {
		return true;
	}

}

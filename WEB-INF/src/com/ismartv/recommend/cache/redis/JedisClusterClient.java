package com.ismartv.recommend.cache.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.JedisCluster;

public class JedisClusterClient implements JedisClient {

	private JedisCluster jedisCluster = null;

	public JedisClusterClient() {
		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		poolConfig.setMaxIdle(32);
		poolConfig.setMinIdle(1);
		poolConfig.setMaxTotal(128);
		poolConfig.setMaxWaitMillis(1000);
		poolConfig.setMinEvictableIdleTimeMillis(900000);
		poolConfig.setTimeBetweenEvictionRunsMillis(100000);

		jedisCluster = new JedisCluster(JedisConfig.getInstance()
				.getHostAndPorts(), poolConfig);
	}

	@Override
	public String get(String region, String key) {
		String result = jedisCluster.get(region + "_" + key);
		return result;
	}

	@Override
	public boolean set(String region, String key, String value) {
		jedisCluster.setex(region + "_" + key, 3600, value);
		return true;
	}
}

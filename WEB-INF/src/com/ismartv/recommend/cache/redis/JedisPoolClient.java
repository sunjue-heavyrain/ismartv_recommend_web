package com.ismartv.recommend.cache.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.Hashing;

public class JedisPoolClient implements JedisClient {

	private ShardedJedisPool pool;

	public JedisPoolClient() {
		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		poolConfig.setMaxIdle(32);
		poolConfig.setMinIdle(1);
		poolConfig.setMaxTotal(128);
		poolConfig.setMaxWaitMillis(1000);
		poolConfig.setMinEvictableIdleTimeMillis(900000);
		poolConfig.setTimeBetweenEvictionRunsMillis(100000);

		pool = new ShardedJedisPool(poolConfig, JedisConfig.getInstance()
				.getJedisShardInfos(), Hashing.MURMUR_HASH);
	}

	@Override
	public String get(String region, String key) {
		ShardedJedis shardedJedis = null;
		String result = null;
		boolean tf = false;
		try {
			shardedJedis = pool.getResource();
			result = shardedJedis.get(region + "_" + key);
			tf = true;
		} catch (Exception e) {
			e.printStackTrace();
			tf = false;
		} finally {
			if (shardedJedis != null) {
				if (tf) {
					pool.returnResource(shardedJedis);
				} else {
					pool.returnBrokenResource(shardedJedis);
				}
			}
			shardedJedis = null;
		}
		return result;
	}

	@Override
	public boolean set(String region, String key, String value) {
		ShardedJedis shardedJedis = null;
		boolean tf = false;
		try {
			shardedJedis = pool.getResource();
			shardedJedis.setex(region + "_" + key, 3600, value);
			tf = true;
		} catch (Exception e) {
			e.printStackTrace();
			tf = false;
		} finally {
			if (shardedJedis != null) {
				if (tf) {
					pool.returnResource(shardedJedis);
				} else {
					pool.returnBrokenResource(shardedJedis);
				}
			}
			shardedJedis = null;
		}
		return tf;
	}

}

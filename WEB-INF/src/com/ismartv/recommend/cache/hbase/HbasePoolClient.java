package com.ismartv.recommend.cache.hbase;

import org.apache.commons.pool2.impl.GenericKeyedObjectPool;

public class HbasePoolClient {
	private GenericKeyedObjectPool<String, HbaseClient> pool;

	public HbasePoolClient() {
		pool = new GenericKeyedObjectPool<String, HbaseClient>(
				new PooledHbaseFactory());
		pool.setMaxIdlePerKey(500);
		pool.setMinIdlePerKey(1);
		pool.setMaxWaitMillis(1000);
		pool.setMaxTotal(2500);
		pool.setMinEvictableIdleTimeMillis(900000);
		pool.setTimeBetweenEvictionRunsMillis(100000);
	}

	public String get(String region, String key) {
		HbaseClient hbaseClient = null;
		String result = null;
		boolean tf = false;
		try {
			hbaseClient = pool.borrowObject(region, 500);
			result = hbaseClient.get(key);
			tf = true;
		} catch (Exception e) {
			e.printStackTrace();
			tf = false;
		} finally {
			if (hbaseClient != null) {
				if (tf) {
					pool.returnObject(region, hbaseClient);
				} else {
					try {
						pool.invalidateObject(region, hbaseClient);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
			hbaseClient = null;
		}
		return result;
	}

	public boolean set(String region, String key, String value) {
		return true;
	}
}

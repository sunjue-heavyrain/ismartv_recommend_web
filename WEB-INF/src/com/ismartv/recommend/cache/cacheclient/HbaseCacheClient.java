package com.ismartv.recommend.cache.cacheclient;

import com.ismartv.recommend.cache.CacheClient;
import com.ismartv.recommend.cache.hbase.HbasePoolClient;

public class HbaseCacheClient implements CacheClient {

	private HbasePoolClient hbasePoolClient;

	public HbaseCacheClient() {
		hbasePoolClient = new HbasePoolClient();
	}

	@Override
	public String get(String region, String key) {
		String result = hbasePoolClient.get(region, key);
		return result;
	}

	@Override
	public boolean set(String region, String key, String value) {
		return true;
	}

	/**
	 * @return the hbasePoolClient
	 */
	public HbasePoolClient getHbasePoolClient() {
		return hbasePoolClient;
	}

	/**
	 * @param hbasePoolClient
	 *            the hbasePoolClient to set
	 */
	public void setHbasePoolClient(HbasePoolClient hbasePoolClient) {
		this.hbasePoolClient = hbasePoolClient;
	}

	@Override
	public boolean isInMemory() {
		return false;
	}

}

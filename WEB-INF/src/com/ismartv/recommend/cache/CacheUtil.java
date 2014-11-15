package com.ismartv.recommend.cache;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.ismartv.recommend.cache.cacheclient.HbaseCacheClient;
import com.ismartv.recommend.cache.cacheclient.MemoryCacheClient;
import com.ismartv.recommend.cache.cacheclient.RedisCacheClient;

@Repository
public class CacheUtil {

	private static final CacheUtil instance = new CacheUtil();

	private List<CacheClient> lstCacheClients = new ArrayList<CacheClient>(0);

	private CacheUtil() {
		lstCacheClients.add(new MemoryCacheClient());
		lstCacheClients.add(new RedisCacheClient());
		lstCacheClients.add(new HbaseCacheClient());
	}

	public static CacheUtil getInstance() {
		return instance;
	}

	public String get(String region, String key) {
		return get(region, key, false);
	}

	public String get(String region, String key, boolean inMemory) {
		String result = null;
		int i = 0;
		for (; i < lstCacheClients.size(); i++) {
			if (!inMemory || lstCacheClients.get(i).isInMemory()) {
				result = lstCacheClients.get(i).get(region, key);
				if (result != null) {
					break;
				}
			}
		}
		if (i > 0 && result != null) {
			for (i--; i >= 0; i--) {
				if (!inMemory || lstCacheClients.get(i).isInMemory()) {
					lstCacheClients.get(i).set(region, key, result);
				}
			}
		}
		return result;
	}

	public boolean set(String region, String key, String value) {
		for (int i = lstCacheClients.size() - 1; i >= 0; i--) {
			if (lstCacheClients.get(i).isInMemory()) {
				lstCacheClients.get(i).set(region, key, value);
			}
		}
		return true;
	}

	/**
	 * @return the lstCacheClients
	 */
	public List<CacheClient> getLstCacheClients() {
		return lstCacheClients;
	}

	/**
	 * @param lstCacheClients
	 *            the lstCacheClients to set
	 */
	public void setLstCacheClients(List<CacheClient> lstCacheClients) {
		this.lstCacheClients = lstCacheClients;
	}
}

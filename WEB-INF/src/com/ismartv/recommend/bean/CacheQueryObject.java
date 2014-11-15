package com.ismartv.recommend.bean;

public class CacheQueryObject {

	private String region;
	private String key;
	private boolean inMemory;
	private String contextKey;

	/**
	 * @return the region
	 */
	public String getRegion() {
		return region;
	}

	/**
	 * @param region
	 *            the region to set
	 */
	public void setRegion(String region) {
		this.region = region;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the inMemory
	 */
	public boolean isInMemory() {
		return inMemory;
	}

	/**
	 * @param inMemory
	 *            the inMemory to set
	 */
	public void setInMemory(boolean inMemory) {
		this.inMemory = inMemory;
	}

	/**
	 * @return the contextKey
	 */
	public String getContextKey() {
		return contextKey;
	}

	/**
	 * @param contextKey
	 *            the contextKey to set
	 */
	public void setContextKey(String contextKey) {
		this.contextKey = contextKey;
	}

}

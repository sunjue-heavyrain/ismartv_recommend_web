package com.ismartv.recommend.chain;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ismartv.recommend.bean.CacheQueryObject;
import com.ismartv.recommend.cache.CacheUtil;

public class CacheGetCommand implements Command {

	public static final String CONTEXT_OBJECT_KEY = "cache";

	private static final Logger LOGGER = Logger
			.getLogger(CacheGetCommand.class);

	@Autowired
	@Qualifier("cacheUtil")
	private CacheUtil cacheUtil;

	@Override
	public boolean execute(Context context) throws Exception {
		List<CacheQueryObject> lst = (List<CacheQueryObject>) context
				.get(CONTEXT_OBJECT_KEY);
		if (lst == null) {
			return false;
		}

		for (CacheQueryObject cacheObject : lst) {
			String value = cacheUtil.get(cacheObject.getRegion(),
					cacheObject.getKey(), cacheObject.isInMemory());
			context.put(cacheObject.getContextKey(), value);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("region=" + cacheObject.getRegion() + "\tkey="
						+ cacheObject.getKey() + "\tisInMemory="
						+ cacheObject.isInMemory() + "\tvalue=" + value);
			}
		}

		context.remove(CONTEXT_OBJECT_KEY);

		return false;
	}

	/**
	 * @return the cacheUtil
	 */
	public CacheUtil getCacheUtil() {
		return cacheUtil;
	}

	/**
	 * @param cacheUtil
	 *            the cacheUtil to set
	 */
	public void setCacheUtil(CacheUtil cacheUtil) {
		this.cacheUtil = cacheUtil;
	}

}

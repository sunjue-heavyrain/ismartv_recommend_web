package com.ismartv.recommend.chain;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alibaba.fastjson.JSONObject;
import com.ismartv.recommend.bean.CacheQueryObject;
import com.ismartv.recommend.worker.ItemInfoGet;

public class SectionExistCommand implements Command {

	public static final String CONTEXT_OBJECT_KEY = "section_exist";

	private static final Logger LOGGER = Logger
			.getLogger(SectionExistCommand.class);

	@Autowired
	@Qualifier("itemInfoGet")
	private ItemInfoGet itemInfoGet;

	@Override
	public boolean execute(Context context) throws Exception {
		String sectionOrder = (String) context
				.get(ChainConstant.CONTEXT_SECTION_ORDER);
		if (!(sectionOrder == null || sectionOrder.isEmpty())) {
			return false;
		}

		CacheQueryObject cacheObject = (CacheQueryObject) context
				.get(CONTEXT_OBJECT_KEY);
		if (cacheObject == null) {
			return false;
		}

		JSONObject jsonObject = itemInfoGet.getSections(cacheObject.getKey(),
				true);

		context.put(cacheObject.getContextKey(), jsonObject.toJSONString());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("\tkey=" + cacheObject.getKey() + "\tvalue="
					+ jsonObject);
		}

		context.remove(CONTEXT_OBJECT_KEY);

		return false;
	}

	/**
	 * @return the itemInfoGet
	 */
	public ItemInfoGet getItemInfoGet() {
		return itemInfoGet;
	}

	/**
	 * @param itemInfoGet
	 *            the itemInfoGet to set
	 */
	public void setItemInfoGet(ItemInfoGet itemInfoGet) {
		this.itemInfoGet = itemInfoGet;
	}

}

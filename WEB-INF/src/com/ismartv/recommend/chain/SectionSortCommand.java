package com.ismartv.recommend.chain;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ismartv.recommend.utils.Constant;

public class SectionSortCommand implements Command {

	private static final Logger LOGGER = Logger
			.getLogger(SectionSortCommand.class);

	@Override
	public boolean execute(Context context) throws Exception {
		String sectionOrder = (String) context
				.get(ChainConstant.CONTEXT_SECTION_ORDER);
		String userOrder = (String) context
				.get(ChainConstant.CONTEXT_USER_ORDER);
		String channel = (String) context.get(ChainConstant.CONTEXT_CHANNEL);
		JSONObject jsonUserOrder = getJsonObject(userOrder);
		JSONArray jsonUserChannelSection = jsonUserOrder.getJSONArray(channel);
		if (jsonUserChannelSection == null) {
			jsonUserChannelSection = new JSONArray();
		}

		JSONObject jsonSectionOrder = getJsonObject(sectionOrder);
		JSONArray jsonSourceOrder = jsonSectionOrder
				.getJSONArray(Constant.SECTION_JSON_KEY_ORDER);
		JSONObject jsonContent = jsonSectionOrder
				.getJSONObject(Constant.SECTION_JSON_KEY_CONTENT);
		JSONObject jsonSourceFix = jsonSectionOrder
				.getJSONObject(Constant.SECTION_JSON_KEY_FIX);
		JSONObject jsonSourceFixSlug = jsonSectionOrder
				.getJSONObject(Constant.SECTION_JSON_KEY_FIXSLUG);

		if (jsonSectionOrder == null || jsonSectionOrder.isEmpty()
				|| jsonContent == null || jsonContent.isEmpty()) {
			context.put("result", new JSONArray(0));
			return false;
		}
		if (jsonSourceFix == null) {
			jsonSourceFix = new JSONObject();
		}
		if (jsonSourceFixSlug == null) {
			jsonSourceFixSlug = new JSONObject();
		}

		JSONArray result = new JSONArray(jsonContent.size());
		for (int userIdx = 0; userIdx < jsonUserChannelSection.size()
				&& !jsonContent.isEmpty(); userIdx++) {
			String slug = null;
			while ((slug = jsonSourceFix.getString(Integer.toString(result
					.size()))) != null) {
				result.add(jsonContent.remove(slug));
			}

			slug = jsonUserChannelSection.getString(userIdx);
			if (jsonSourceFixSlug.get(slug) == null) {
				Object tmp = jsonContent.remove(slug);
				if (tmp != null) {
					result.add(tmp);
				}
			}
		}
		for (int sourceIdx = 0; sourceIdx < jsonSourceOrder.size()
				&& !jsonContent.isEmpty(); sourceIdx++) {
			String slug = null;
			while ((slug = jsonSourceFix.getString(Integer.toString(result
					.size()))) != null) {
				result.add(jsonContent.remove(slug));
			}

			slug = jsonSourceOrder.getString(sourceIdx);
			Object tmp = jsonContent.remove(slug);
			if (tmp != null) {
				result.add(tmp);
			}
		}

		context.put(ChainConstant.CONTEXT_RESULT, result);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("result=" + result);
		}

		return false;
	}

	private JSONObject getJsonObject(String userOrder) {
		if (userOrder == null || userOrder.isEmpty()) {
			return new JSONObject();
		} else {
			try {
				return JSONObject.parseObject(userOrder);
			} catch (Exception e) {
				return new JSONObject();
			}
		}
	}

}

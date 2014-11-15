package com.ismartv.recommend.chain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.ChainBase;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.configuration.MapConfiguration;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ismartv.recommend.bean.CacheQueryObject;
import com.ismartv.recommend.cache.CacheClient;
import com.ismartv.recommend.cache.CacheUtil;
import com.ismartv.recommend.cache.cacheclient.MemoryCacheClient;
import com.ismartv.recommend.utils.Constant;
import com.ismartv.recommend.worker.ItemInfoGet;

public class MyChainBase extends ChainBase {

	public void setCommands(Command[] commands) {
		if (!(commands == null || commands.length == 0)) {
			super.commands = commands;
		}
	}

	public void setCommands(List<Command> commands) {
		if (!(commands == null || commands.isEmpty())) {
			super.commands = commands.toArray(new Command[commands.size()]);
		}
	}

	public static void main(String[] args) {
		CacheUtil cacheUtil = CacheUtil.getInstance();
		ArrayList<CacheClient> lstCacheClient = new ArrayList<CacheClient>(1);
		lstCacheClient.add(new MemoryCacheClient());
		cacheUtil.setLstCacheClients(lstCacheClient);

		JSONObject jsonUserOrder = new JSONObject();
		JSONArray jsonUserSectionOrder = new JSONArray();
		jsonUserSectionOrder.add("tvbju");
		jsonUserSectionOrder.add("paihengbang");
		jsonUserSectionOrder.add("rementuijian_1");
		jsonUserSectionOrder.add("xingzhen");
		jsonUserOrder.put("teleplay", jsonUserSectionOrder);
		jsonUserSectionOrder = new JSONArray();
		jsonUserSectionOrder.add("dianshijupaixingbang");
		jsonUserOrder.put("rankinglist", jsonUserSectionOrder);
		cacheUtil.set("user_section", "123", jsonUserOrder.toJSONString());

		MapConfiguration propertiesConfiguration = new MapConfiguration(
				new HashMap<Object, Object>());
		propertiesConfiguration.addProperty(Constant.TV_SERIES,
				"A11,A21,ALPHA2,K81,K82,K91,K91V1,S31,S52,S9");
		propertiesConfiguration.addProperty(
				Constant.TV_CHANNEL_REST_URL_PATTERN,
				"http://tvSeries.cord.tvxio.com/api/tv/channels/");
		propertiesConfiguration.addProperty(
				Constant.TV_CHANNEL_REST_URL_SERIES, "tvSeries");

		ItemInfoGet itemInfoGet = new ItemInfoGet();
		itemInfoGet.setCacheUtil(cacheUtil);
		itemInfoGet.setPropertiesConfiguration(propertiesConfiguration);
		itemInfoGet.setChannelSectionsCache();

		CacheGetCommand cacheGetCommand = new CacheGetCommand();
		cacheGetCommand.setCacheUtil(cacheUtil);

		ContextBase contextBase = new ContextBase();
		ArrayList<CacheQueryObject> lstCacheQueryObjects = new ArrayList<CacheQueryObject>(
				1);
		CacheQueryObject cacheQueryObject = new CacheQueryObject();
		cacheQueryObject.setContextKey("section_order");
		cacheQueryObject.setInMemory(true);
		cacheQueryObject
				.setKey("http://cord.tvxio.com/api/tv/sections/teleplay/");
		cacheQueryObject.setRegion("section_order");
		lstCacheQueryObjects.add(cacheQueryObject);
		cacheQueryObject = new CacheQueryObject();
		cacheQueryObject.setContextKey("user_order");
		cacheQueryObject.setInMemory(true);
		cacheQueryObject.setKey("123");
		cacheQueryObject.setRegion("user_section");
		lstCacheQueryObjects.add(cacheQueryObject);
		contextBase.put("cache", lstCacheQueryObjects);
		contextBase.put("channel", "teleplay");

		MyChainBase mcb = new MyChainBase();
		mcb.addCommand(cacheGetCommand);
		mcb.addCommand(new SectionSortCommand());

		try {
			mcb.execute(contextBase);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(contextBase.get("result"));
	}

}

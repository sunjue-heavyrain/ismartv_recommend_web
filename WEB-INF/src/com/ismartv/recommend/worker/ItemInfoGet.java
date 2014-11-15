package com.ismartv.recommend.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ismartv.recommend.cache.CacheClient;
import com.ismartv.recommend.cache.CacheUtil;
import com.ismartv.recommend.cache.cacheclient.MemoryCacheClient;
import com.ismartv.recommend.chain.ChainConstant;
import com.ismartv.recommend.chain.SectionSortCommand;
import com.ismartv.recommend.utils.Constant;

public class ItemInfoGet {

	private static final Logger LOGGER = Logger.getLogger(ItemInfoGet.class);

	// private static final String CHANNEL_URL =
	// "http://tvSeries.cord.tvxio.com/api/tv/channels/";
	// private static final String CHANNEL_URL_SERIES = "tvSeries";

	@Autowired
	@Qualifier("cacheUtil")
	private CacheUtil cacheUtil;

	@Autowired
	@Qualifier("propertiesConfiguration")
	private Configuration propertiesConfiguration;

	private Set<String> newSectionUrl = Collections
			.synchronizedSet(new HashSet<String>());

	private HttpClient httpClient;

	public static void main(String[] args) {
		ItemInfoGet itemInfoGet = new ItemInfoGet();

		itemInfoGet.propertiesConfiguration = new MapConfiguration(
				new HashMap<Object, Object>());
		// itemInfoGet.propertiesConfiguration.addProperty(Constant.TV_SERIES,
		// "A11,A21,ALPHA2,K81,K82,K91,K91V1,S31,S52,S9");
		itemInfoGet.propertiesConfiguration.addProperty(
				Constant.TV_CHANNEL_REST_URL_PATTERN,
				"http://tvSeries.cord.tvxio.com/api/tv/channels/");
		itemInfoGet.propertiesConfiguration.addProperty(
				Constant.TV_CHANNEL_REST_URL_SERIES, "tvSeries");
		itemInfoGet.propertiesConfiguration
				.addProperty(Constant.TV_SECTION_REST_URL_HOST_REPLACE_KEY,
						"cord.tvxio.com");
		itemInfoGet.propertiesConfiguration.addProperty(
				Constant.TV_SECTION_REST_URL_HOST_REPLACE_VALUE,
				"cord.tvxio.com");

		ArrayList<CacheClient> lstCacheClient = new ArrayList<CacheClient>();
		lstCacheClient.add(new MemoryCacheClient());
		itemInfoGet.cacheUtil = CacheUtil.getInstance();
		itemInfoGet.cacheUtil.setLstCacheClients(lstCacheClient);

		itemInfoGet.init();

		Object sectionOrder = itemInfoGet.cacheUtil.get(
				Constant.CACHE_REGION_SECTION_ORDER,
				"http://cord.tvxio.com/api/tv/sections/chinesemovie/");
		String userOrder = "{\"chinesemovie\":[\"fanzui_1_2\",\"jingcaipianhua_1\"]}";
		String channel = "chinesemovie";

		ContextBase contextBase = new ContextBase();
		contextBase.put(ChainConstant.CONTEXT_CHANNEL, channel);
		contextBase.put(ChainConstant.CONTEXT_USER_ORDER, userOrder);
		contextBase.put(ChainConstant.CONTEXT_SECTION_ORDER, sectionOrder);

		try {
			new SectionSortCommand().execute(contextBase);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String result = "";
		Object o = contextBase.get(ChainConstant.CONTEXT_RESULT);
		if (o instanceof String) {
			result = (String) o;
		} else if (o instanceof JSON) {
			result = JSON.toJSONString(o, SerializerFeature.PrettyFormat);
		}

		System.out.println(result);
	}

	public void init() {
		MultiThreadedHttpConnectionManager cm = new MultiThreadedHttpConnectionManager();
		cm.getParams().setMaxTotalConnections(100);
		cm.getParams().setDefaultMaxConnectionsPerHost(7);
		cm.getParams().setSoTimeout(10000);
		cm.getParams().setConnectionTimeout(10000);
		httpClient = new HttpClient(cm);
		httpClient.getParams().setSoTimeout(10000);
		httpClient.getParams().setParameter(
				HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		// 设置成了默认的恢复策略，在发生异常时候将自动重试3次，在这里你也可以设置成自定义的恢复策略
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());

		setChannelSectionsCache();
	}

	private String getURLContentByHttpClient(String strUrl) {
		GetMethod get = new GetMethod(strUrl);

		// 设置成了默认的恢复策略，在发生异常时候将自动重试3次，在这里你也可以设置成自定义的恢复策略
		get.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());

		int responseStat = 0;
		String responseBody = "";
		try {
			responseStat = httpClient.executeMethod(get);
			if (responseStat == HttpStatus.SC_OK) {
				// byte[] responseBody = get.getResponseBody();
				responseBody = get.getResponseBodyAsString();
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			get.releaseConnection();
			get = null;
		}

		return responseBody;
	}

	public void setChannelSectionsCache() {
		long l = System.currentTimeMillis();

		String[] arrTvSeries = propertiesConfiguration
				.getStringArray(Constant.TV_SERIES);
		String channelUrl = propertiesConfiguration
				.getString(Constant.TV_CHANNEL_REST_URL_PATTERN);
		String channelUrlSeries = propertiesConfiguration
				.getString(Constant.TV_CHANNEL_REST_URL_SERIES);

		setInfoCache(channelUrl.replace(channelUrlSeries + ".", ""));

		if (!(arrTvSeries == null || arrTvSeries.length == 0)) {
			for (String tvSeries : arrTvSeries) {
				setInfoCache(channelUrl.replace(channelUrlSeries, tvSeries));
			}
		}

		if ((!(newSectionUrl.isEmpty()))) {
			for (String strSectionUrl : newSectionUrl) {
				getSections(strSectionUrl, false);
			}
		}

		l = System.currentTimeMillis() - l;
		LOGGER.info("get channel sections cache in " + l + "ms");
	}

	private void setInfoCache(String channelUrl) {
		JSONArray jsonChannels = getChannel(channelUrl);
		for (int i = 0; i < jsonChannels.size(); i++) {
			JSONObject jsonChannel = jsonChannels.getJSONObject(i);
			String strSectionsUrl = jsonChannel.getString("url");
			JSONObject jsonSection = getSections(strSectionsUrl, false);
			String strJsonSection = jsonSection.toJSONString();
			LOGGER.debug("setInfoCache:" + strSectionsUrl + ":"
					+ strJsonSection);
			// if (cacheUtil != null) {
			// cacheUtil.set("section_order", strSectionsUrl, strJsonSection);
			// } else {
			// System.out.println(strSectionsUrl + ":" + strJsonSection);
			// }
		}
	}

	private JSONArray getChannel(String channelUrl) {
		JSONArray jsonArray = JSONArray
				.parseArray(getURLContentByHttpClient(replaceHost(channelUrl)));
		JSONArray result = new JSONArray(jsonArray.size());
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			String channelId = jsonObject.getString("channel");
			if (channelId == null || channelId.isEmpty()
					|| channelId.charAt(0) == '$') {
				continue;
			}
			JSONObject jsonChannel = new JSONObject();
			jsonChannel.put("channel", channelId);
			jsonChannel.put("name", jsonObject.get("name"));
			jsonChannel.put("url", jsonObject.getString("url"));
			result.add(jsonChannel);
		}
		return result;
	}

	public JSONObject getSections(String strSectionsUrl, boolean isNewSectionUrl) {
		JSONArray jsonArray = JSONArray
				.parseArray(getURLContentByHttpClient(replaceHost(strSectionsUrl)));
		if (jsonArray == null) {
			System.out.println("---null section:" + strSectionsUrl);
			return new JSONObject();
		}

		if (isNewSectionUrl) {
			newSectionUrl.add(strSectionsUrl);
		}

		JSONObject jsonContent = new JSONObject();
		JSONArray jsonOrder = new JSONArray(jsonArray.size());
		JSONObject jsonFix = new JSONObject();
		JSONObject jsonFixSlug = new JSONObject();
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			String slug = jsonObject.getString("slug");
			boolean fix = jsonObject.getBooleanValue("fixed");

			jsonContent.put(slug, jsonObject);
			if (fix) {
				jsonFix.put(Integer.toString(i), slug);
				jsonFixSlug.put(slug, "");
			}
			jsonOrder.add(slug);
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(Constant.SECTION_JSON_KEY_FIX, jsonFix);
		jsonObject.put(Constant.SECTION_JSON_KEY_FIXSLUG, jsonFixSlug);
		jsonObject.put(Constant.SECTION_JSON_KEY_ORDER, jsonOrder);
		jsonObject.put(Constant.SECTION_JSON_KEY_CONTENT, jsonContent);

		if (cacheUtil != null) {
			cacheUtil.set(Constant.CACHE_REGION_SECTION_ORDER, strSectionsUrl,
					jsonObject.toJSONString());
		}

		return jsonObject;
	}

	private String replaceHost(String url) {
		String replaceKey = propertiesConfiguration
				.getString(Constant.TV_SECTION_REST_URL_HOST_REPLACE_KEY);
		String replaceHost = propertiesConfiguration
				.getString(Constant.TV_SECTION_REST_URL_HOST_REPLACE_VALUE);
		return url.replace(replaceKey, replaceHost);
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

	/**
	 * @return the propertiesConfiguration
	 */
	public Configuration getPropertiesConfiguration() {
		return propertiesConfiguration;
	}

	/**
	 * @param propertiesConfiguration
	 *            the propertiesConfiguration to set
	 */
	public void setPropertiesConfiguration(Configuration propertiesConfiguration) {
		this.propertiesConfiguration = propertiesConfiguration;
	}

}

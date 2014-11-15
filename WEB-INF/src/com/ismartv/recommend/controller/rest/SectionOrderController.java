package com.ismartv.recommend.controller.rest;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.chain.impl.ChainBase;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ismartv.recommend.bean.CacheQueryObject;
import com.ismartv.recommend.chain.CacheGetCommand;
import com.ismartv.recommend.chain.ChainConstant;
import com.ismartv.recommend.chain.SectionExistCommand;
import com.ismartv.recommend.utils.Constant;

@Controller
@RequestMapping(value = "/tv", method = { RequestMethod.GET, RequestMethod.POST })
public class SectionOrderController {

	private static final Logger LOGGER = Logger
			.getLogger(SectionOrderController.class);

	@Autowired
	@Qualifier("sectionOrderChain")
	private ChainBase sectionOrderChain;

	@Autowired
	@Qualifier("propertiesConfiguration")
	private Configuration propertiesConfiguration;

	@ResponseBody
	@RequestMapping("/sections/{channel}")
	public Object sectionOrder(@PathVariable("channel") String channel,
			Model model, HttpServletRequest request,
			HttpServletResponse response) {

		long l = System.currentTimeMillis();

		ContextBase contextBase = new ContextBase();
		ArrayList<CacheQueryObject> lstCacheQueryObjects = new ArrayList<CacheQueryObject>();
		contextBase.put(CacheGetCommand.CONTEXT_OBJECT_KEY,
				lstCacheQueryObjects);

		contextBase.put(ChainConstant.CONTEXT_CHANNEL, channel);

		boolean isNeedAbTest = propertiesConfiguration.getBoolean(
				Constant.ABTEST_NEED, false);

		String sectionUrl = propertiesConfiguration
				.getString(Constant.TV_SECTION_REST_URL_PATTERN);
		String sectionUrlReplaceHostPort = propertiesConfiguration
				.getString(Constant.TV_SECTION_REST_URL_REPLACE_HOST_PORT);
		String defaultSectionUrlHost = propertiesConfiguration
				.getString(Constant.TV_SECTION_REST_URL_HOST_DEFAULT);
		String defaultSectionUrlPort = propertiesConfiguration
				.getString(Constant.TV_SECTION_REST_URL_PORT_DEFAULT);
		String sectionUrlReplaceChannel = propertiesConfiguration
				.getString(Constant.TV_SECTION_REST_URL_REPLACE_CHANNEL);
		String replaceHost = propertiesConfiguration
				.getString(Constant.TV_SECTION_REST_URL_HOST_REPLACE_KEY);
		String host = request.getHeader("Host");
		if (host != null) {
			if (host.indexOf(replaceHost) < 0) {
				LOGGER.error("ERROR HOST:" + host);
				host = defaultSectionUrlHost;
			}
			int idx = host.indexOf(':');
			if (idx > 0) {
				host = host.substring(0, idx);
			}
		} else {
			host = defaultSectionUrlHost;
		}
		if (StringUtils.isNotEmpty(defaultSectionUrlPort)
				&& StringUtils.isNumeric(defaultSectionUrlPort)) {
			host = new StringBuilder(host.length()
					+ defaultSectionUrlPort.length() + 1).append(host)
					.append(':').append(defaultSectionUrlPort).toString();
		}
		String requestUrl = sectionUrl.replace(sectionUrlReplaceHostPort, host)
				.replace(sectionUrlReplaceChannel, channel);

		String userAgent = request.getHeader("User-Agent");

		String sn = "";
		boolean isCustomSort = false;
		// String tvSeries = "";
		if (!(userAgent == null || userAgent.isEmpty())) {
			int idx = userAgent.lastIndexOf(' ');
			if (idx > 0) {// SN
				sn = userAgent.substring(idx + 1);
				// CacheQueryObject cacheQueryObject = new CacheQueryObject();
				// cacheQueryObject
				// .setContextKey(ChainConstant.CONTEXT_USER_ORDER);
				// cacheQueryObject.setInMemory(false);
				// cacheQueryObject.setKey(sn);
				// cacheQueryObject
				// .setRegion(Constant.CACHE_REGION_USER_SECTION_ORDER);
				// if (isNeedAbTest) {
				// if ((sn.hashCode() & 1) == 1) {
				// lstCacheQueryObjects.add(cacheQueryObject);
				// }
				// } else {
				// lstCacheQueryObjects.add(cacheQueryObject);
				// }
				if (!isNeedAbTest || (sn.hashCode() & 1) == 1) {
					isCustomSort = true;
					CacheQueryObject cacheQueryObject = new CacheQueryObject();
					cacheQueryObject
							.setContextKey(ChainConstant.CONTEXT_USER_ORDER);
					cacheQueryObject.setInMemory(false);
					cacheQueryObject.setKey(sn);
					cacheQueryObject
							.setRegion(Constant.CACHE_REGION_USER_SECTION_ORDER);
					lstCacheQueryObjects.add(cacheQueryObject);
				}
			}

			// idx = userAgent.indexOf('/');
			// if (idx > 0) {// tv series
			// tvSeries = userAgent.substring(0, idx);
			// idx = tvSeries.indexOf('_');
			// if (idx > 0) {
			// tvSeries = tvSeries.substring(idx + 1);
			// }
			// boolean b = false;
			// if (!tvSeries.isEmpty()) {
			// String[] arrTvSeries = propertiesConfiguration
			// .getStringArray(Constant.TV_SERIES);
			// for (String string : arrTvSeries) {
			// if (string.equalsIgnoreCase(tvSeries)) {
			// b = true;
			// break;
			// }
			// }
			// }
			// if (b) {
			// requestUrl = sectionUrl.replace(sectionUrlSeries,
			// tvSeries.toLowerCase());
			// } else {
			// StringBuilder sb = new StringBuilder(
			// sectionUrlSeries.length() + 1);
			// sb.append(sectionUrlSeries).append(".");
			// requestUrl = sectionUrl.replace(sb.toString(), "");
			// }
			// } else {
			// StringBuilder sb = new StringBuilder(
			// sectionUrlSeries.length() + 1);
			// sb.append(sectionUrlSeries).append(".");
			// requestUrl = sectionUrl.replace(sb.toString(), "");
			// }
		}
		// if (requestUrl.isEmpty()) {
		// StringBuilder sb = new StringBuilder(sectionUrlSeries.length() + 1);
		// sb.append(sectionUrlSeries).append(".");
		// requestUrl = sectionUrl.replace(sb.toString(), "");
		// }
		// requestUrl = requestUrl.replace(sectionUrlChannel, channel);

		CacheQueryObject cacheQueryObject = new CacheQueryObject();
		cacheQueryObject.setContextKey(ChainConstant.CONTEXT_SECTION_ORDER);
		cacheQueryObject.setInMemory(true);
		cacheQueryObject.setKey(requestUrl);
		cacheQueryObject.setRegion(Constant.CACHE_REGION_SECTION_ORDER);
		lstCacheQueryObjects.add(cacheQueryObject);
		contextBase.put(SectionExistCommand.CONTEXT_OBJECT_KEY,
				cacheQueryObject);

		try {
			sectionOrderChain.execute(contextBase);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String result = "";
		Object o = contextBase.get(ChainConstant.CONTEXT_RESULT);
		if (o instanceof String) {
			result = (String) o;
		} else if (o instanceof JSON) {
			result = JSON.toJSONString(o, SerializerFeature.BrowserCompatible);
		}

		l = System.currentTimeMillis() - l;

		if (LOGGER.isInfoEnabled()) {
			// LOGGER.info("spend_time=" + l + "ms\tsn=" + sn + "\ttvSeries="
			// + tvSeries + "\tchannel=" + channel + "\trequestUrl="
			// + requestUrl + "\tresult=" + result);
			LOGGER.info("spend_time=" + l + "ms\tsn=" + sn + "\thost=" + host
					+ "\tchannel=" + channel + "\tcustomSort=" + isCustomSort
					+ "\trequestUrl=" + requestUrl);
		}

		return result;
	}
}

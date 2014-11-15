package com.ismartv.recommend.controller.rest;

import java.util.Arrays;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ismartv.recommend.cache.CacheUtil;

@Controller
@RequestMapping(value = "/recomm", method = { RequestMethod.GET,
		RequestMethod.POST })
public class RecommController {

	@Autowired
	@Qualifier("cacheUtil")
	private CacheUtil cacheUtil;

	/**
	 * http://127.0.0.1:8080/ismartv_recomm_web/recomm/i2i/test.json?num=3
	 * 
	 * @param itemId
	 * @param num
	 * @param model
	 * @param request
	 * @param response
	 * @return {"item":["item1","item2","item3"]}
	 */
	@RequestMapping("/i2i/{itemId}")
	public Object itemRecomm(
			@PathVariable("itemId") String itemId,
			@RequestParam(value = "num", required = false, defaultValue = "10") int num,
			Model model, HttpServletRequest request,
			HttpServletResponse response) {
		String str = cacheUtil.get("item_recomm", itemId);
		JSONArray result = new JSONArray();
		if (!StringUtils.isEmpty(str)) {
			JSONArray ja = JSONArray.parseArray(str);
			if (ja != null) {
				for (int i = 0; i < ja.size() && result.size() < num; i++) {
					JSONArray tmp = ja.getJSONArray(i);
					result.add(tmp.getString(0));
				}
			}
		}
		model.addAttribute(itemId, result);

		return "itemRecomm";
	}

	/**
	 * http://127.0.0.1:8080/ismartv_recomm_web/recomm/hot/test.json?num=3
	 * 
	 * @param channel
	 * @param num
	 * @param model
	 * @param request
	 * @param response
	 * @return {"channel":["item1","item2","item3"]}
	 */
	@RequestMapping("/hot/{channel}")
	public Object itemHotRecomm(
			@PathVariable("channel") String channel,
			@RequestParam(value = "num", required = false, defaultValue = "10") int num,
			Model model, HttpServletRequest request,
			HttpServletResponse response) {
		String str = cacheUtil.get("hot_recomm", channel);
		JSONArray result = new JSONArray();
		if (!StringUtils.isEmpty(str)) {
			JSONArray ja = JSONArray.parseArray(str);
			if (ja != null) {
				for (int i = 0; i < ja.size() && result.size() < num; i++) {
					JSONArray tmp = ja.getJSONArray(i);
					result.add(tmp.getString(0));
				}
			}
		}
		model.addAttribute(channel, result);

		return "hotRecomm";
	}

	/**
	 * http://127.0.0.1:8080/ismartv_recomm_web/recomm/new.json?num=3&his=item1&
	 * his=item2
	 * 
	 * @param num
	 * @param history
	 * @param model
	 * @param request
	 * @param response
	 * @return {"new":["item3","item4","item5"]}
	 */
	@RequestMapping("/new")
	public Object itemNewRecomm(
			@RequestParam(value = "num", required = false, defaultValue = "10") int num,
			@RequestParam(value = "his", required = false) String[] history,
			Model model, HttpServletRequest request,
			HttpServletResponse response) {
		String str = cacheUtil.get("new_recomm", "new");
		JSONArray result = new JSONArray();
		HashSet<String> historySet = new HashSet<String>();
		if (!(history == null || history.length == 0)) {
			historySet.addAll(Arrays.asList(history));
		}
		if (!StringUtils.isEmpty(str)) {
			JSONArray ja = JSONArray.parseArray(str);
			if (ja != null) {
				for (int i = 0; i < ja.size() && result.size() < num; i++) {
					String itemId = ja.getString(i);
					if (!historySet.contains(itemId)) {
						result.add(itemId);
					}
				}
			}
		}
		model.addAttribute("new", result);

		return "newRecomm";
	}

	/**
	 * http://127.0.0.1:8080/ismartv_recomm_web/recomm/u2i/userId.json?num=3&his
	 * = item2&his=item3
	 * 
	 * @param userId
	 * @param num
	 * @param history
	 * @param model
	 * @param request
	 * @param response
	 * @return {"userId":["item1","item4","item5"]}
	 */
	@RequestMapping("/u2i/{userId}")
	public Object userRecomm(
			@PathVariable("userId") String userId,
			@RequestParam(value = "num", required = false) String channel,
			@RequestParam(value = "num", required = false, defaultValue = "10") int num,
			@RequestParam(value = "his", required = false) String[] history,
			Model model, HttpServletRequest request,
			HttpServletResponse response) {
		String str = cacheUtil.get("user_recomm", userId);
		JSONArray result = new JSONArray();
		HashSet<String> historySet = new HashSet<String>();
		if (!(history == null || history.length == 0)) {
			historySet.addAll(Arrays.asList(history));
		}
		if (!StringUtils.isEmpty(str)) {
			JSONObject jo = JSONObject.parseObject(str);
			JSONArray ja = jo
					.getJSONArray(channel == null || channel.isEmpty() ? "ALL"
							: channel);
			if (ja != null) {
				for (int i = 0; i < ja.size() && result.size() < num; i++) {
					JSONArray tmp = ja.getJSONArray(i);
					String itemId = tmp.getString(0);
					if (!historySet.contains(itemId)) {
						result.add(itemId);
					}
				}
			}
		}
		model.addAttribute(userId, result);

		return "userRecomm";
	}
}

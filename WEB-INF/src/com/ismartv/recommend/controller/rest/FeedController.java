package com.ismartv.recommend.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;

@Controller
public class FeedController {
	@RequestMapping("/jsonfeed")
	public String getJSON(Model model) {
		List<TournamentContent> tournamentList = new ArrayList<TournamentContent>();
		tournamentList.add(TournamentContent.generateContent("FIFA",
				new Date(), "World Cup", "www.fifa.com/worldcup/"));
		tournamentList.add(TournamentContent.generateContent("FIFA",
				new Date(), "U-20 World Cup", "www.fifa.com/u20worldcup/"));
		tournamentList.add(TournamentContent.generateContent("FIFA",
				new Date(), "U-17 World Cup", "www.fifa.com/u17worldcup/"));
		tournamentList.add(TournamentContent.generateContent("FIFA",
				new Date(), "Confederations Cup",
				"www.fifa.com/confederationscup/"));
		model.addAttribute("items", tournamentList);
		model.addAttribute("status", 0);
		return "jsontournamenttemplate";
	}

	@ResponseBody
	@RequestMapping("/string/{id}")
	public String getJSON(@PathVariable("id") long id,
			HttpServletRequest request, HttpServletResponse response) {
		System.out.println("------------" + id + "----------");
		List<TournamentContent> tournamentList = new ArrayList<TournamentContent>();
		tournamentList.add(TournamentContent.generateContent("FIFA",
				new Date(), "World Cup", "www.fifa.com/worldcup/"));
		tournamentList.add(TournamentContent.generateContent("FIFA",
				new Date(), "U-20 World Cup", "www.fifa.com/u20worldcup/"));
		tournamentList.add(TournamentContent.generateContent("FIFA",
				new Date(), "U-17 World Cup", "www.fifa.com/u17worldcup/"));
		tournamentList.add(TournamentContent.generateContent("FIFA",
				new Date(), "Confederations Cup",
				"www.fifa.com/confederationscup/"));
		return JSON.toJSONString(tournamentList);
	}

	@RequestMapping("/object/{id}")
	public Object getJSON(@PathVariable("id") String id,
			HttpServletRequest request, HttpServletResponse response) {
		System.out.println("------------" + id + "----------");
		List<TournamentContent> tournamentList = new ArrayList<TournamentContent>();
		tournamentList.add(TournamentContent.generateContent("FIFA",
				new Date(), "World Cup", "www.fifa.com/worldcup/"));
		tournamentList.add(TournamentContent.generateContent("FIFA",
				new Date(), "U-20 World Cup", "www.fifa.com/u20worldcup/"));
		tournamentList.add(TournamentContent.generateContent("FIFA",
				new Date(), "U-17 World Cup", "www.fifa.com/u17worldcup/"));
		tournamentList.add(TournamentContent.generateContent("FIFA",
				new Date(), "Confederations Cup",
				"www.fifa.com/confederationscup/"));
		try {
			System.out.println(new ObjectMapper().writeValueAsString(tournamentList));
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tournamentList;
	}
}

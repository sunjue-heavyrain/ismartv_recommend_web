package com.ismartv.recommend.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class ResponseTimeFilter implements Filter {

	private Logger logger = Logger.getLogger(ResponseTimeFilter.class);

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse reponse,
			FilterChain filterChain) throws IOException, ServletException {
		long l = System.currentTimeMillis();
		filterChain.doFilter(request, reponse);
		l = System.currentTimeMillis() - l;
		if (logger.isDebugEnabled()) {
			HttpServletRequest req = (HttpServletRequest) request;
			System.out.println(req.getRequestURL() + ":" + l);
			logger.debug(req.getRequestURL() + ":" + l);
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}

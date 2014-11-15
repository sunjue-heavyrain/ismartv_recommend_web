package com.ismartv.recommend.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.log4j.Logger;

public final class HttpUtil {

	private static final String DEFAULT_CHARSET_IN_CONTENT_TYPE = "charset=";

	private static Logger logger = Logger.getLogger(HttpUtil.class);

	public static String getURLContentByHttpClient(String strUrl)
			throws Exception {
		logger.info("get Url by HttpClient; url=" + strUrl);
		HttpClient client = new HttpClient();

		client.getParams().setSoTimeout(10000);

		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
				"UTF-8");

		GetMethod get = new GetMethod(strUrl);

		// 设置成了默认的恢复策略，在发生异常时候将自动重试3次，在这里你也可以设置成自定义的恢复策略
		get.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());

		int responseStat = 0;
		String responseBody = "";
		try {
			responseStat = client.executeMethod(get);
			if (responseStat == HttpStatus.SC_OK) {
				// byte[] responseBody = get.getResponseBody();
				responseBody = get.getResponseBodyAsString();
			}
		} catch (HttpException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			get.releaseConnection();
			get = null;
			client = null;
		}

		return responseBody;
	}

	public static String getURLContentByURLConnection(String strUrl) {
		logger.info("get Url by URLConnection; url=" + strUrl);
		String result = "";
		BufferedInputStream in = null;
		try {
			URL url = new URL(strUrl);
			URLConnection conn = url.openConnection();
			conn.connect();
			in = new BufferedInputStream(conn.getInputStream());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			while (in.read(buf) != -1) {
				out.write(buf);
			}
			buf = null;
			result = EncodingUtil.getString(out.toByteArray(),
					getCharSet(conn.getContentType()));
			out = null;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			in = null;
		}
		return result;
	}

	private static String getCharSet(String contentType) {
		String charSet = "UTF-8";
		if (contentType != null) {
			contentType = contentType.trim();
			if (contentType.length() > 0) {
				String charSetTag = DEFAULT_CHARSET_IN_CONTENT_TYPE;
				int index = contentType.indexOf(charSetTag);
				if (index != -1) {
					int endIndex = contentType.indexOf(";", index);
					if (endIndex == -1) {
						charSet = contentType.substring(index
								+ charSetTag.length());
					} else {
						charSet = contentType.substring(
								index + charSetTag.length(), endIndex);
					}
					charSet = charSet.trim();
				}
			}
		}

		return charSet;
	}

	public static String getUrlContent(String url) {
		String body = "";

		try {
			body = getURLContentByHttpClient(url);
		} catch (Exception e) {
			body = getURLContentByURLConnection(url);
		}

		return body;
	}
}

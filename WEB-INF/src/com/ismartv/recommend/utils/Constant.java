package com.ismartv.recommend.utils;

public interface Constant {

	String HBASE_MASTER = "hbase.master";
	String HBASE_ZOOKEEPER_QUORUM = "hbase.zookeeper.quorum";

	String TV_SERIES = "tv.series";
	String TV_CHANNEL_REST_URL_PATTERN = "tv.channel.rest.url.pattern";
	String TV_CHANNEL_REST_URL_SERIES = "tv.channel.rest.url.series";

	String TV_SECTION_REST_URL_PATTERN = "tv.section.rest.url.pattern";
	String TV_SECTION_REST_URL_REPLACE_HOST_PORT = "tv.section.rest.url.replace.host.port";
	String TV_SECTION_REST_URL_REPLACE_CHANNEL = "tv.section.rest.url.replace.channel";
	String TV_SECTION_REST_URL_HOST_DEFAULT = "tv.section.rest.url.host.default";
	String TV_SECTION_REST_URL_PORT_DEFAULT = "tv.section.rest.url.port.default";
	String TV_SECTION_REST_URL_HOST_REPLACE_KEY = "tv.section.rest.url.host.replace.key";
	String TV_SECTION_REST_URL_HOST_REPLACE_VALUE = "tv.section.rest.url.host.replace.value";

	String SECTION_JSON_KEY_FIX = "fix";
	String SECTION_JSON_KEY_FIXSLUG = "fixslug";
	String SECTION_JSON_KEY_ORDER = "order";
	String SECTION_JSON_KEY_CONTENT = "content";

	String CACHE_REGION_SECTION_ORDER = "section_order";
	String CACHE_REGION_USER_SECTION_ORDER = "user_section";

	String ABTEST_NEED = "abtest.need";
}

package com.ismartv.recommend.cache.redis;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisShardInfo;

public class JedisConfig {
	private String configFile = "";

	private List<JedisShardInfo> jedisShardInfos = new ArrayList<JedisShardInfo>(
			0);
	private Set<HostAndPort> hostAndPorts = new HashSet<HostAndPort>();

	private static JedisConfig instance = new JedisConfig();

	private JedisConfig() {

	}

	public static JedisConfig getInstance() {
		return instance;
	}

	public void init() {
		List<String> lst = new ArrayList<String>(0);
		try {
			lst = Files.readAllLines(Paths.get(configFile),
					Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		Logger.getLogger(this.getClass()).info("jedis_config=" + lst);

		if (lst.size() > 0) {
			jedisShardInfos = new ArrayList<JedisShardInfo>(lst.size());
			hostAndPorts = new HashSet<HostAndPort>();
			for (String line : lst) {
				line = line.trim();
				if (line.isEmpty()) {
					continue;
				}
				// ip:port:redisName
				String[] str = line.split(":");
				if (str.length == 2) {
					hostAndPorts.add(new HostAndPort(str[0], Integer
							.parseInt(str[1])));
					jedisShardInfos.add(new JedisShardInfo(str[0], Integer
							.parseInt(str[1])));
				} else if (str.length == 3) {
					hostAndPorts.add(new HostAndPort(str[0], Integer
							.parseInt(str[1])));
					jedisShardInfos.add(new JedisShardInfo(str[0], Integer
							.parseInt(str[1]), str[2]));
				}
			}
		}
	}

	/**
	 * @return the jedisShardInfos
	 */
	public List<JedisShardInfo> getJedisShardInfos() {
		return jedisShardInfos;
	}

	/**
	 * @return the hostAndPorts
	 */
	public Set<HostAndPort> getHostAndPorts() {
		return hostAndPorts;
	}

	/**
	 * @param hostAndPorts
	 *            the hostAndPorts to set
	 */
	public void setHostAndPorts(Set<HostAndPort> hostAndPorts) {
		this.hostAndPorts = hostAndPorts;
	}

	/**
	 * @return the configFile
	 */
	public String getConfigFile() {
		return configFile;
	}

	/**
	 * @param configFile
	 *            the configFile to set
	 */
	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}
}

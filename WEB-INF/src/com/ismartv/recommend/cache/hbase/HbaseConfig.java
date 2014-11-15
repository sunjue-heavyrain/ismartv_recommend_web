package com.ismartv.recommend.cache.hbase;

import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;

import com.ismartv.recommend.utils.Constant;

public class HbaseConfig {

	// private String hbaseMaster = "hadoopns410:60000";
	// private String hbaseZookeeper = "hadoopns410";
	private FileConfiguration fileConfig = new PropertiesConfiguration();

	// private Configuration hbaseConfiguration;

	private static final HbaseConfig instance = new HbaseConfig();

	private HbaseConfig() {

	}

	public static HbaseConfig getInstance() {
		return instance;
	}

	public void init() {
		// hbaseConfiguration = HBaseConfiguration.create();
		// // hbaseConfiguration.set("hbase.master", hbaseMaster);
		// // hbaseConfiguration.set("hbase.zookeeper.quorum", hbaseZookeeper);
		// hbaseConfiguration.set("hbase.master",
		// fileConfig.getString("hbase.master"));
		// hbaseConfiguration.set("hbase.zookeeper.quorum",
		// fileConfig.getString("hbase.zookeeper.quorum"));
	}

	/**
	 * @return the hbaseConfiguration
	 */
	public Configuration create() {
		Configuration conf = HBaseConfiguration.create();
		conf.set("hbase.master", fileConfig.getString(Constant.HBASE_MASTER));
		conf.set("hbase.zookeeper.quorum",
				fileConfig.getString(Constant.HBASE_ZOOKEEPER_QUORUM));

		return conf;
	}

	/**
	 * @return the fileConfig
	 */
	public FileConfiguration getFileConfig() {
		return fileConfig;
	}

	/**
	 * @param fileConfig
	 *            the fileConfig to set
	 */
	public void setFileConfig(FileConfiguration fileConfig) {
		this.fileConfig = fileConfig;
	}

	// /**
	// * @return the hbaseMaster
	// */
	// public String getHbaseMaster() {
	// return hbaseMaster;
	// }
	//
	// /**
	// * @param hbaseMaster
	// * the hbaseMaster to set
	// */
	// public void setHbaseMaster(String hbaseMaster) {
	// this.hbaseMaster = hbaseMaster;
	// }
	//
	// /**
	// * @return the hbaseZookeeper
	// */
	// public String getHbaseZookeeper() {
	// return hbaseZookeeper;
	// }
	//
	// /**
	// * @param hbaseZookeeper
	// * the hbaseZookeeper to set
	// */
	// public void setHbaseZookeeper(String hbaseZookeeper) {
	// this.hbaseZookeeper = hbaseZookeeper;
	// }

}

package com.ismartv.recommend.cache.hbase;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.util.Bytes;

import com.alibaba.fastjson.JSONArray;

public class HbaseClient {

	private static final byte[] HTABLE_DATA = "data".getBytes();

	private HTable hTable;

	public HbaseClient(String htableName) throws IOException {
		try {
			hTable = new HTable(HbaseConfig.getInstance().create(), htableName);
		} catch (IOException e) {
			hTable = null;
		}
	}

	public String get(String key) throws IOException {
		if (hTable == null) {
			JSONArray array = new JSONArray();
			array.add(key);
			return array.toJSONString();
			// return null;
		}

		return Bytes.toString(hTable.get(new Get(Bytes.toBytes(key))).getValue(
				HTABLE_DATA, HTABLE_DATA));

	}

	public void close() {
		if (hTable != null) {
			try {
				hTable.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		hTable = null;
	}

}

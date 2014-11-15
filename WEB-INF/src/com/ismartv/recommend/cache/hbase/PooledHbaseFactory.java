package com.ismartv.recommend.cache.hbase;

import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class PooledHbaseFactory extends
		BaseKeyedPooledObjectFactory<String, HbaseClient> {

	@Override
	public HbaseClient create(String key) throws Exception {
		return new HbaseClient(key);
	}

	@Override
	public PooledObject<HbaseClient> wrap(HbaseClient hbaseClient) {
		return new DefaultPooledObject<HbaseClient>(hbaseClient);
	}

	@Override
	public void destroyObject(String key, PooledObject<HbaseClient> p)
			throws Exception {
		super.destroyObject(key, p);
		p.getObject().close();
	}

}

package com.alibaba.doris.dataserver.store.innodb.db;

import junit.framework.TestCase;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.KeyFactory;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.ValueFactory;
import com.alibaba.doris.dataserver.store.innodb.config.InnoDBDatabaseConfiguration;

public class InnoDBDataBaseTest extends TestCase {
    public void testMock() {

    }
	public void t1estSetAndGet() {
		InnoDBDatabaseConfiguration conf = new InnoDBDatabaseConfiguration();
		conf.setSchema("ajun");
		conf.setFilePerTableEnabled(true);
		
		InnoDBBuilder builder = new InnoDBBuilder(conf);
		builder.initDataBase();
		InnoDBDataBase db = new InnoDBDataBase(builder, "001");
		db.open();
		
		InnoDBDataBase db2 = new InnoDBDataBase(builder, "002");
		db2.open();
		
		try{
			Key key = KeyFactory.createKey(1, "key1",1);
			Value value =ValueFactory.createValue("value".getBytes(), System.currentTimeMillis());
			db.set(key, value);
			db2.set(key, value);
			
			Value newValue = db.get(key);
			assertNotNull(newValue);
			assertEquals(value, newValue);
			
			newValue = db2.get(key);
			assertNotNull(newValue);
			assertEquals(value, newValue);
			
			assertTrue(db.delete(key));
			newValue = db.get(key);
			assertNull(newValue);
		}catch(Exception e){
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
		
	}
}

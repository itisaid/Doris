package com.alibaba.doris.dataserver.store.innodb.db;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.KeyFactory;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.ValueFactory;
import com.alibaba.doris.dataserver.store.innodb.config.InnoDBDatabaseConfiguration;

public class PressureTestForInnoDB {

    /**
     * @param args
     */
    public static void main(String[] args) {
        InnoDBDatabaseConfiguration conf = new InnoDBDatabaseConfiguration();
        conf.setSchema("ajun");
        conf.setFilePerTableEnabled(true);

        InnoDBBuilder builder = new InnoDBBuilder(conf);
        builder.initDataBase();
        InnoDBDataBase db = new InnoDBDataBase(builder, "bar");
        db.open();

        try {
            Key key = KeyFactory.createKey(1, "key1", 1);
            Value value = ValueFactory.createValue("value".getBytes(), System.currentTimeMillis());

            int len = 10000;

            long start = System.currentTimeMillis();
            for (int i = 0; i < len; i++) {
                key = KeyFactory.createKey(1, "key1" + i, 1);
                db.set(key, value);
            }
            long end = System.currentTimeMillis();
            System.out.println("Total(ms):" + (end - start) + " count:" + len + " avg:" + ((end - start) / len));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

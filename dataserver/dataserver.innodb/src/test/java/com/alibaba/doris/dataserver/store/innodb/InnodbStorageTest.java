package com.alibaba.doris.dataserver.store.innodb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;

import com.alibaba.doris.common.config.ConfigTools;
import com.alibaba.doris.dataserver.store.Storage;
import com.alibaba.doris.dataserver.store.StorageConfig;
import com.alibaba.doris.dataserver.store.StorageDriver;
import com.alibaba.doris.dataserver.store.StorageTestUnit;
import com.alibaba.doris.dataserver.store.innodb.config.InnoDBDatabaseConfiguration;

public class InnodbStorageTest extends StorageTestUnit {
 
	@Override
	protected Storage getStorage() {
		return this.storage;
	} 

	@Before
    public void setUp() throws Exception {
        clear();
        
        try {
            driver = (StorageDriver) InnoDBStorageDriver.class.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        StorageConfig config = getStorageConfig();
        driver.init(config);
        InnoDBDatabaseConfiguration innodbConf = ((InnoDBStorageDriver)driver).getConfig();
        innodbConf.setDataHomeDir(getDatabasePath());
        innodbConf.setLogFileHomeDirectory(getDatabasePath());
        storage = driver.createStorage();
        
        super.setUp();
    }
	
	public StorageConfig getStorageConfig() {
        String path = ConfigTools.getCurrentClassPath(this.getClass());
        StorageConfig config = new StorageConfig();
        config.setPropertiesFile(path + File.separatorChar + "innodb.properties");
        config.setSize(5000);
        config.setStorageDriverClass("com.alibaba.doris.dataserver.store.innodb.InnoDBStorageDriver");
        config.setStorageTypeClass("");
        return config;
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }
    
    private String getDatabasePath(){
    	return InnodbStorageTest.class.getClassLoader().getResource("").getPath() + "innodb_test" + File.separatorChar;
    }

    private void clear() {
        try {
            Thread.sleep(5);
            FileUtils.forceDelete(new File(getDatabasePath()));
        } catch (FileNotFoundException ignore) {
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
        }
        
        File f = new File(getDatabasePath());
        f.mkdir();

    }
    
    private StorageDriver driver;
    private Storage       storage;
}

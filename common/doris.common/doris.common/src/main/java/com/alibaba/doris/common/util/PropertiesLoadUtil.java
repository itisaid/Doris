package com.alibaba.doris.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PropertiesLoadUtil {
    private static final Log logger = LogFactory.getLog(PropertiesLoadUtil.class);

    public static Properties loadProperties(String propLocation) {

        Properties properties = null;
        
        properties = loadAsFile(propLocation);
        if( properties != null)
        	return properties;
        
        properties = loadAsResource(propLocation);

        return properties;
    }

	private static Properties loadAsResource(String propLocation) {
		//load config & parse config
		Properties properties = null;
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(propLocation);
        if (is == null) {
        	return null;
            //throw new IllegalArgumentException("cannot find property location:" + propLocation);
        }

        try {
            properties = new Properties();
            properties.load(is);
        } catch (IOException e) {
            logger.error("load config failed" + propLocation, e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                logger.error("closing input stream failed.", e);
            }
        }
		return properties;
	}

	private static Properties loadAsFile(String propLocation) {
		Properties properties = null;
		
		File file = new File( propLocation );
		
		if( file.exists() && file.isFile()) {
			
			 InputStream is = null;
			 
			 try {
				is = new FileInputStream(file);
			     properties = new Properties();
		         properties.load(is);
		         
		         if(logger.isInfoEnabled())
		        	 logger.info( String.format("Load config  as file, %s" ,  propLocation));
		         
			} catch (Exception e) {
				 logger.warn( String.format("Fail to load config  as file, %s, cause: %s" ,  propLocation, e.getMessage()));
			}finally {
				 try {    is.close();  } catch (IOException e) {    }
			}
		}
		return properties;
	}
}

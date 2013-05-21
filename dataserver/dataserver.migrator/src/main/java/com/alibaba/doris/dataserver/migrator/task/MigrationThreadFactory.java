/**
 * 
 */
package com.alibaba.doris.dataserver.migrator.task;

import java.lang.reflect.Field;
import java.util.concurrent.ThreadFactory;

/**
 * @author raymond
 *
 */
public class MigrationThreadFactory implements ThreadFactory {

	/**
	 * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
	 */
	public Thread newThread(Runnable r) {
		try {
			Field[] fields = r.getClass().getDeclaredFields();
			
			Field firstTaskField = null;
			for (Field field : fields) {
				if( field.getName().equals("firstTask")) {
					firstTaskField = field;
					break;
				}
			}
			firstTaskField.setAccessible(true);
			
			BaseMigrationTask task = (BaseMigrationTask) firstTaskField.get( r );
			String taskName = task.getName(); 
			
			Thread thread = new Thread( r , taskName);
			return thread;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}

/*
Copyright(C) 2010-2011 Alibaba Group Holding Limited
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.alibaba.doris.dataserver.migrator.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.dataserver.migrator.MigrationManager;

/**
 * DefaultMigrationListener.
 * 迁移默认监听器. 执行实际的迁移工作。
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-25
 */
public class DefaultMigrationListener implements MigrationListener {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultMigrationListener.class);
	
	private String name = "DefaultMigrationListener";	
	
	private MigrationManager migrationManager;
	
	public String getMigrationListenerName() {
		return name;
	}

	public void setMigrationManager(MigrationManager migrationManager) {
		this.migrationManager = migrationManager;		
	}
	
	public MigrationManager getMigrationManager() {
		return migrationManager;
	}
	
	/**
	 * @see com.alibaba.doris.dataserver.migrator.event.MigrationListener#onMigrationStart(com.alibaba.doris.dataserver.migrator.event.MigrationEvent)
	 */
	public void onMigrationStart(MigrationEvent event) {
		if(logger.isDebugEnabled())
			logger.debug("onMigrationStart " + event.getMigrateType() + ",status:" + event.getMigrateStatus() +", progress:" + event.getProgress() + ". MigrateRoutePairs size: "  + event.getMigrateRoutePairs().size());
		
		migrationManager.getMigrationReporter().report( event );
	}
	
	
	/**
	 * @see com.alibaba.doris.dataserver.migrator.event.MigrationListener#onMigraionProcessing(com.alibaba.doris.dataserver.migrator.event.MigrationEvent)
	 */
	public void onMigraionProcessing(MigrationEvent event) {
		if(logger.isDebugEnabled())
			logger.debug("onMigraionProcessing  " + event.getMigrateType() + ",status:" + event.getMigrateStatus() +", progress:" + event.getProgress() + ". MigrateRoutePairs size: "  + event.getMigrateRoutePairs().size() + ", task: " + event.getMigrationTask());
		
		migrationManager.getMigrationReporter().report( event );
	}

	/**
	 * @see com.alibaba.doris.dataserver.migrator.event.MigrationListener#onMigrationNodeFinished(com.alibaba.doris.dataserver.migrator.event.MigrationEvent)
	 */
	public void onMigrationNodeFinished(MigrationEvent event) {
		if(logger.isDebugEnabled())
			logger.debug("onMigrationNodeFinished " + event.getMigrateType() + ",status:" + event.getMigrateStatus() +", progress:" + event.getProgress() + ", task: " + event.getMigrationTask());
		
//		new Throwable().printStackTrace();
		migrationManager.getMigrationReporter().report( event );
	}
	
	/**
	 * @see com.alibaba.doris.dataserver.migrator.event.MigrationListener#onMigrationAllFinished(com.alibaba.doris.dataserver.migrator.event.MigrationEvent)
	 */
	public void onMigrationAllFinished(MigrationEvent event) {
		if(logger.isDebugEnabled())
			logger.debug("onMigrationAllFinished - " + event.getMigrateType() + ",status:" + event.getMigrateStatus() +", progress:" + event.getProgress());
		migrationManager.getMigrationReporter().report( event );
	}

	/**
	 * @see com.alibaba.doris.dataserver.migrator.event.MigrationListener#onMigrationCancelled(com.alibaba.doris.dataserver.migrator.event.MigrationEvent)
	 */
	public void onMigrationCancelled(MigrationEvent event) {
		if(logger.isDebugEnabled())
			logger.debug("onMigrationCancelled " + event.getMigrateType() + ",status:" + event.getMigrateStatus() +", progress:" + event.getProgress());
		
		migrationManager.getMigrationReporter().report( event );
	}

	public void onDataCleanStart(MigrationEvent event) {
		if(logger.isDebugEnabled())
			logger.debug("onMigrationDataCleanStart " + event.getMigrateType() + ",status:" + event.getMigrateStatus() +", progress:" + event.getProgress());
		
		migrationManager.getMigrationReporter().report( event );
	}
	
	public void onDataCleanProcessing(MigrationEvent event) {
		if(logger.isDebugEnabled())
			logger.debug("onMigrationDataCleanProcessing " + event.getMigrateType() + ",status:" + event.getMigrateStatus() +", progress:" + event.getProgress());
		
		migrationManager.getMigrationReporter().report( event );		
	}
	
	public void onDataCleanFinish(MigrationEvent event) {
		if(logger.isDebugEnabled())
			logger.debug("onMigrationDataCleanFinish " + event.getMigrateType() + ",status:" + event.getMigrateStatus() +", progress:" + event.getProgress());
		
		migrationManager.getMigrationReporter().report( event );		
	}
	
	/**
	 * 迁移失败
	 */
	public void onMigrationFail(MigrationEvent event) {
		if(logger.isDebugEnabled())
			logger.debug("onMigrationFail " + event.getMigrateType() + ",status:" + event.getMigrateStatus() +", progress:" + event.getProgress());
		
		migrationManager.getMigrationReporter().report( event );				
	}
	
	public void onDataCleanError(MigrationEvent event) {
		if(logger.isDebugEnabled())
			logger.debug("onDataCleanError " + event.getMigrateType() + ",status:" + event.getMigrateStatus() +", progress:" + event.getProgress());
		
		migrationManager.getMigrationReporter().report( event );						
	}
	
	
	
	public void onExitMigrationTask(MigrationEvent event) {
        ;// Nothing to do;
    }

    public boolean equals(Object obj) {
		return obj!=null 
		&& ( obj instanceof DefaultMigrationListener) 
		&& ((DefaultMigrationListener)obj).getMigrationListenerName().equals( this.getMigrationListenerName());
	}

}

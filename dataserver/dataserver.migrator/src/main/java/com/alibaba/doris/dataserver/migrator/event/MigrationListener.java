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

import com.alibaba.doris.dataserver.migrator.MigrationManager;

/**
 * MigrationListener
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-25
 */
public interface MigrationListener {
	
	String getMigrationListenerName();
	
	void setMigrationManager(MigrationManager migrationManager);
	
	MigrationManager getMigrationManager();
	
	void onMigrationStart(MigrationEvent event);
	
	void onMigraionProcessing(MigrationEvent event);
	
	void onMigrationCancelled(MigrationEvent event);
	
	void onMigrationNodeFinished(MigrationEvent event);
	
	void onMigrationAllFinished(MigrationEvent event);
	
	void onDataCleanStart(MigrationEvent event);
	
	void onDataCleanProcessing(MigrationEvent event);
	
	void onDataCleanFinish(MigrationEvent event);

	void onMigrationFail(MigrationEvent event);

	void onDataCleanError(MigrationEvent event);
	
	void onExitMigrationTask(MigrationEvent event);
}

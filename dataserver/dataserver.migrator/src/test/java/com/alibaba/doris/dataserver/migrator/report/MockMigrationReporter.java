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
package com.alibaba.doris.dataserver.migrator.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.common.MigrateStatusEnum;
import com.alibaba.doris.dataserver.migrator.event.MigrationEvent;
import com.alibaba.doris.dataserver.migrator.mock.MockMigrateReportService;

/**
 * MockMigrationReporter
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-6-15
 */
public class MockMigrationReporter extends DefaultMigrationReporter {
	
	private static final Logger logger = LoggerFactory.getLogger(MockMigrationReporter.class);
	
	public MockMigrationReporter() {
		super();
		setMigrateReportService( new MockMigrateReportService());
	}
	
	@Override
	protected void report2AdminServer(MigrationEvent event, String targetId, MigrateStatusEnum reportStatus, String message) {
		if( logger.isDebugEnabled()) {
			logger.debug("Migrate report mockly:" + event.getServerPort() +",targetId:" + targetId +", progress:" + event.getProgress() + ",status:" + reportStatus + ",message:" + message );
		}
	}
}

/*
Copyright(C) 2010 Alibaba Group Holding Limited
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
package com.alibaba.doris.dataserver.migrator.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.common.MigrateStatusEnum;
import com.alibaba.doris.common.adminservice.MigrateReportService;
import com.alibaba.doris.dataserver.migrator.report.MockMigrationReporter;

/**
 * @author Raymond He ( He Kun), raymond.he.kk@gmail.com
 * @since 1.0
 * 2011-6-28
 */
public class MockMigrateReportService implements MigrateReportService {

	private static final Logger logger = LoggerFactory.getLogger(MockMigrateReportService.class);
	
	/**
	 * @see com.alibaba.doris.common.adminservice.MigrateReportService#report(java.lang.String, java.lang.String, int, com.alibaba.doris.common.MigrateStatusEnum, java.lang.String)
	 */
	public String report(String srcPhysicalId, String targetPhysicalId,
			int schedule, MigrateStatusEnum status, String message) {
				
		return "";
	}

}

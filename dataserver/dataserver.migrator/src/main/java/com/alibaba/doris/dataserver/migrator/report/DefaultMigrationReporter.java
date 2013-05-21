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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.common.MigrateStatusEnum;
import com.alibaba.doris.common.adminservice.MigrateReportService;
import com.alibaba.doris.common.adminservice.PostMigrateReportService;
import com.alibaba.doris.common.migrate.NodeMigrateStatus;
import com.alibaba.doris.common.route.MigrationRoutePair;
import com.alibaba.doris.dataserver.migrator.event.DefaultMigrationListener;
import com.alibaba.doris.dataserver.migrator.event.MigrationEvent;

/**
 * DefaultMigrationReporter
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-31
 */
public class DefaultMigrationReporter implements MigrationReporter {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultMigrationListener.class);
	
	private MigrateReportService migrateReportService;
	private PostMigrateReportService postMigrateReportService;
	
	public DefaultMigrationReporter() {
	}
	/**
	 * 报告迁移事件.
	 * @see com.alibaba.doris.dataserver.migrator.report.MigrationReporter#report(java.lang.String, int)
	 */
	public void report(MigrationEvent event) {
		
		NodeMigrateStatus migrateStatus = event.getMigrateStatus();
		
		if( migrateStatus == null) {
			if( logger.isDebugEnabled())
				logger.debug("MigrationReporter event is null. Don't report.");
			return ;
		}
		
		List<MigrationRoutePair>  routePairs = event.getMigrateRoutePairs();
		
		Map<String,String> migrateTargetMap = new HashMap<String, String>();
		for ( MigrationRoutePair pair : routePairs  ) {
			//在本次报告中，会报告所有 vnode 的迁移进度信息。 而对于已经报告过的target，不重复报告
			String targetId = pair.getTargetPhysicalId();
			if( !migrateTargetMap.containsKey( pair.getTargetPhysicalId())) {
				reportMigratePair(event, pair);
				
				migrateTargetMap.put(targetId , targetId);
			}			
		}
	}

	/**
	 * @param event
	 * @param pair
	 */
	protected void reportMigratePair(MigrationEvent event, MigrationRoutePair pair) {

		String targetId = pair.getTargetPhysicalId();
		
		NodeMigrateStatus status = event.getMigrateStatus();
		
		MigrateStatusEnum reportStatus = null;
		
		if( event.isFailed() ) {
			
			if( event.getMigrateStatus().toString().startsWith("DATA")) {
				reportStatus = MigrateStatusEnum.DATACLEAN_ERROR;
			}else {
				reportStatus = MigrateStatusEnum.MIGERATE_ERROR;
			}
			
		}else {
			if( status == NodeMigrateStatus.MIGRATING ) {
				
				reportStatus = MigrateStatusEnum.MIGERATING;
				
			}else if( status == NodeMigrateStatus.MIGRATE_NODE_FINISHED ) {
				
				reportStatus = MigrateStatusEnum.FINISH;
				
			}else if( status == NodeMigrateStatus.MIGRATE_ALL_FINISHED ) {
				
				reportStatus = MigrateStatusEnum.FINISH;
				
			}else if( status == NodeMigrateStatus.DATACLEANING) {
				
				reportStatus = MigrateStatusEnum.DATACLEANING;
				
			}else if( status  == NodeMigrateStatus.DATACLEAN_FINISH) {
				
				reportStatus = MigrateStatusEnum.DATACLEAN_FINISH ;
			}else {
				logger.warn("Invalid report status of migrate event: " +  event +" , and needn't report." );
			}
		}

		String message = "";
		if (null != reportStatus) {
			message = event.getMessage() != null ? event.getMessage()
					: reportStatus.toString();
		}
		report2AdminServer(event, targetId, reportStatus, message);
	}

	/**
	 * report2AdminServer
	 * @param event
	 * @param targetId
	 * @param reportStatus
	 * @param message
	 */
	protected void report2AdminServer(MigrationEvent event, String targetId, MigrateStatusEnum reportStatus, String message) {
		
		NodeMigrateStatus migrateStatus = event.getMigrateStatus();
		
		String logMsg =  "Report migrate  to AdminServer: \"" + event.getServerPort() +",targetId:" + targetId +", progress:" + event.getProgress() + ",status:" + reportStatus + ",message:" + message +"\"" ;
		try {
			String result = null;
			String sourcePort = String.valueOf( event.getServerPort());
			if( migrateStatus == NodeMigrateStatus.DATACLEANING || migrateStatus == NodeMigrateStatus.DATACLEAN_FINISH ) {
				
				result = postMigrateReportService.report( sourcePort , event.getProgress() , reportStatus , event.getMessage());
			}else {
				
				result = migrateReportService.report( sourcePort, targetId , event.getProgress() ,reportStatus , message );
			}
			
			if( logger.isInfoEnabled()) {
				logMsg = logMsg + ", result:" + result;
				logger.info( logMsg );
			}
		}catch(Throwable t) {
			if( logger.isInfoEnabled()) {
				logger.info( logMsg );
			}
		}
	}
	
	public void setMigrateReportService(MigrateReportService migrateReportService) {
		this.migrateReportService = migrateReportService;
	}
	
	public MigrateReportService getMigrateReportService() {
		return migrateReportService;
	}
	
	public PostMigrateReportService getPostMigrateReportService() {
		return postMigrateReportService;
	}
	
	public void setPostMigrateReportService(PostMigrateReportService postMigrateReportService) {
		this.postMigrateReportService = postMigrateReportService;
	}
}

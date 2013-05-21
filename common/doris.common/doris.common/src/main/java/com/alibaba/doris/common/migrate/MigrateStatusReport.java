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
package com.alibaba.doris.common.migrate;


/**
 * MigrateStatusReport
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-31
 */
public class MigrateStatusReport {
	
	private String migrateType;
	private String Status ;
	private int progress;
	private long startTime;
	private long elapseTime;
	
	public String getMigrateType() {
		return migrateType;
	}
	public void setMigrateType(String migrateType) {
		this.migrateType = migrateType;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	public int getProgress() {
		return progress;
	}
	public void setProgress(int progress) {
		this.progress = progress;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getElapseTime() {
		return elapseTime;
	}
	public void setElapseTime(long elapseTime) {
		this.elapseTime = elapseTime;
	}
}

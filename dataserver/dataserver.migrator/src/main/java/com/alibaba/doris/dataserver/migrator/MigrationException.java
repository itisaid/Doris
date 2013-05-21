/*
Copyright(C) 1999-2010 Alibaba Group Holding Limited
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
package com.alibaba.doris.dataserver.migrator;

/**
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0
 * 2011-7-13
 */
public class MigrationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1095563456550007291L;

	/**
	 * 
	 */
	public MigrationException() {
		
	}

	/**
	 * @param message
	 */
	public MigrationException(String message) {
		super(message);
		
	}

	/**
	 * @param cause
	 */
	public MigrationException(Throwable cause) {
		super(cause);
		
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MigrationException(String message, Throwable cause) {
		super(message, cause);
		
	}

}

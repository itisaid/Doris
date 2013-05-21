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
package com.alibaba.doris.algorithm.mirgate;

/**
 * MigrationPair
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-18
 */
public class MigrationPair {
	private Integer source;
	private Integer vnode;
	private Integer target;
	
	public MigrationPair( Integer vnode,Integer source, Integer target) {
		this.vnode = vnode;
		this.source = source;    		
		this.target = target;
	}

	public Integer getSource() {
		return source;
	}

	public Integer getVnode() {
		return vnode;
	}

	public Integer getTarget() {
		return target;
	}

	public String toString() {
		return vnode+ ":" + source +"-" + target ;
	}
}
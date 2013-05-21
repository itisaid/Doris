/*
 * Copyright(C) 2010-2011 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.cli;

import java.util.List;


/**
 * HelpPrinter
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-7-28
 */
public class HelpPrinter {
	private List<Option> options ;
	
	public HelpPrinter(List<Option> options) {
		this.options = options;
	}
	
	public void setOptions(List<Option> options) {
		this.options = options;
	}
	
	public List<Option> getOptions() {
		return options;
	}
	
	public String helpString() {
		StringBuilder help = new StringBuilder();
		
		for (int i = 0; i < options.size(); i++) {
			help.append(options.get(i));
		}
		return help.toString();
	}
}

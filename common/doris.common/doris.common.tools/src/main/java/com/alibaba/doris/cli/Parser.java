/*
 * Copyright(C) 2010-2011 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-7-28
 */
public class Parser {
	
	private List<Option> options;
	private Map<String,Option> optionMap = new HashMap<String, Option>();
	private String[] args;
	
	public CommandLine parse(List<Option> options, String[] args) throws CommandException {
		this.options = options;
		this.args = args;
		
		CommandLine commandLine =  new CommandLine();
		commandLine.setOptions(options);
		
		for (Option option : options) {
			String shortcut = option.getShortcut();
			if( optionMap.containsKey( shortcut )) {
				throw new CommandException("Duplicate Option: " +  shortcut );
			}
			optionMap.put( shortcut , option );
		}
		Map<String, String> valueMap = new HashMap<String, String>();
		
//		List<String> argList = Arrays.asList( args );
//		
//		int optionIndex = 0;
//	
//		for (int i = 0; i < options.size(); i++) {
//			Option option = options.get( optionIndex );
//			
//			if( option.isRequired() &&  (option.getShortcut()==null || !argList.contains( option.getShortcut() )) ) {
//				throw new CommandException("Option missing: " +  option.getShortcut() );
//			}
//			
//			int valueIndex  = argList.indexOf( option.getShortcut() ) + 1;
//			
//			if( option.isHasOptionValue() && args.length == (valueIndex)) {
//				throw new CommandException("Option value missing: " +  option.getShortcut() );
//			}
//			
//			String value =argList.get(  valueIndex  );
//			
//			for (int j = 0; j < options.size(); j++) {
//				Option tempOp = options.get(j);
//				if(tempOp.getShortcut().equals( value )) {
//					throw new CommandException("Option value missing: " +  option.getShortcut() +", value got:" + value);
//				}
//			}
//			
//			valueMap.put(option.getShortcut() , value );
//			
//			optionIndex++;
//		}
//		
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if( arg.startsWith("-")) {
				
				Option option = optionMap.get( arg);
				if( option !=null ) {
					
					if( option.isHasOptionValue()) {
						
						if( i+1 < args.length) {
							String value = args[i+1];
							valueMap.put( arg , value);
							i++;
						}else {
							throw new CommandException("Option and its value missing " + arg );
						}
						
					}
					
				}
			}else {
				throw new CommandException("Invalid Option " + arg);
			}
		}
		
		//check required
		for (Option option : options) {
			
			if( option.isRequired() && !valueMap.containsKey( option.getShortcut())) {
				throw new CommandException("Option and its value missing " +  option.getShortcut() );
			}
		}
		commandLine.setValueMap(valueMap );
		
		return commandLine;
	}
}

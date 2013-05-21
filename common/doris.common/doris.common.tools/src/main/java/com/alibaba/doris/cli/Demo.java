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
import java.util.List;

/**
 * Demo
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-7-28
 */
public class Demo {
	
	public static void main(String[] args) {
		
		String[] demoArgs = new String[]{"-ip","10.20.144.91","-port","9001","-vn","1000","-o","put","-k","101:XYZ001","-v","A0012"};
		List<Option> options = new ArrayList<Option>();
		
		options.add(new Option("-ip", "IP", "DataServer IP"));
		options.add(new Option("-port", "Port", "DataServer Port"));
		options.add(new Option("-vn", "VirtualNumber", "Virtual Number to routing."));
		options.add(new Option("-o", "KV Operation", "put, get, or delete"));
		options.add(new Option("-k", "Key", "Format ns:logicKey, e.g. 1:aaa, 101:XYZ001 "));
		options.add(new Option("-v", "Value ", "e.g. A0012"));
		options.add(new Option("-h", "Help", "Print command usage",false,false));
		Parser  parser = new Parser();
		CommandLine commandLine = null;
		try {
			commandLine = parser.parse(options, demoArgs);
			
			System.out.println(" ip: " + commandLine.getValue("-ip"));
			System.out.println(" port: " + commandLine.getValue("-port"));
			System.out.println(" v: " + commandLine.getValue("-vn"));
			System.out.println(" o: " + commandLine.getValue("-o"));
			System.out.println(" k: " + commandLine.getValue("-k"));
			System.out.println(" v: " + commandLine.getValue("-v"));
			
		} catch (CommandException e) {
			System.err.println("Command error:\r\n" + e.getMessage() );
			System.err.println("help:\r\n" + new HelpPrinter(options).helpString() );
		}
	}
}

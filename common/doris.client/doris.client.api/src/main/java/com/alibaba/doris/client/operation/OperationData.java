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
package com.alibaba.doris.client.operation;

import java.util.List;

import com.alibaba.doris.common.Namespace;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.operation.OperationEnum;
import com.alibaba.doris.common.operation.OperationPolicy;

/**
 * OperationData
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-4-22
 */
public class OperationData {
	
	public final static  int DEFAULT_VNODE = -1;
	
	private Operation operation;
	
	private String command;
	private Namespace namespace;
	private OperationPolicy operationPolicy;
	
	private List<Object> args;
	private int vnode = DEFAULT_VNODE  ;
	
	private Key key;
	
	private Object result;
	private boolean success;
	private int errorCode;
	private String errorMessage;
	private Exception exception;
	
	public OperationData(Operation operation, Namespace namespace,  List<Object> args) {
		this.operation = operation;
		this.namespace = namespace;
		this.args = args;		
	}
	
	public Operation getOperation() {
		return operation;
	}
	
	public void setOperation(Operation operation) {
		this.operation = operation;
	}
	
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	
	public Namespace getNamespace() {
		return namespace;
	}
	
	public void setNamespace(Namespace namespace) {
		this.namespace = namespace;
	}
	
	public OperationPolicy getOperationPolicy() {
		return operationPolicy;
	}
	
	public void setOperationPolicy(OperationPolicy operationPolicy) {
		this.operationPolicy = operationPolicy;
	}
	
	public List<Object> getArgs() {
		return args;
	}
	public void setArgs(List<Object> args) {
		this.args = args;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	public boolean isSuccess() {
		return success;
	}
	
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public Exception getException() {
		return exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}	
	
	public String getLogicKey() {
		return String.valueOf( args.get( 0 ) );
	}
	
	public void setVnode(int vnode) {
		this.vnode = vnode;
	}
	
	public int getVnode() {
		return vnode;
	}
	
	public Key getKey() {
		return key;
	}
	
	public void setKey(Key key) {
		this.key = key;
	}
}

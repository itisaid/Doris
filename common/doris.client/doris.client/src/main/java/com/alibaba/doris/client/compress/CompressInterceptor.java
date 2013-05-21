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
package com.alibaba.doris.client.compress;

import java.util.List;

import com.alibaba.doris.client.operation.OperationData;
import com.alibaba.doris.common.Namespace;
import com.alibaba.doris.common.compress.Compressor;
import com.alibaba.doris.common.compress.GZipCompressor;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.impl.ValueImpl;
import com.alibaba.doris.dproxy.AbstractInterceptor;
import com.alibaba.doris.dproxy.InvokeInfo;

/**
 * 
 * @author frank
 *
 */
public class CompressInterceptor  extends AbstractInterceptor  {
	
	public static final int Default_Compress_Threshold = 1024;
	
	private static final Compressor compressor = new GZipCompressor();
	
	/**
	 * Value压缩. <br/>
	 * 如果 compressMode 为空，不压缩，
	 * 否则，按 gzip 方式压缩, 以 compressThreshold 为阀值. 
	 */
	@Override
	public void before(InvokeInfo info) throws Throwable {
		
		OperationData operationData = (OperationData) info.getArgs()[0];
		Namespace namespace = operationData.getNamespace();		
		String compressMode = namespace.getCompressMode();
	
		List<Object> args =  operationData.getArgs();
		
		Object value = args.get(1);
		
		if( value == null) {
			return ;
		}
		if(!( value instanceof byte[])) {
			throw new IllegalArgumentException("Gotten value to be deserialized must be byte[].");
		}
		
		String thresholdStr = namespace.getCompressThreshold();
		
		int threshold = Default_Compress_Threshold;
		try {
			threshold = Integer.valueOf( thresholdStr ).intValue();
		}catch(Exception e) {
		}
		byte[] bytes = (byte[]) value;		
		
		Value pValue = null;
		if( compressMode != null && bytes.length > threshold ) {
			
			byte[] bytes2 = compressor.compress(bytes);
			pValue = new ValueImpl( bytes2  ,System.currentTimeMillis());
			pValue.setCompressed( true );
			
		}else {
			pValue = new ValueImpl( bytes  ,System.currentTimeMillis());
			pValue.setCompressed( false );
		}
		args.set( 1 , pValue );
	}
}

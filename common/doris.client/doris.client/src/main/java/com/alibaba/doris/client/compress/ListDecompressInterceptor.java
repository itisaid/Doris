/*
 * Copyright(C) 2010-2011 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.client.compress;

import java.util.List;

import com.alibaba.doris.client.operation.OperationData;
import com.alibaba.doris.common.compress.Compressor;
import com.alibaba.doris.common.compress.GZipCompressor;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.dproxy.AbstractInterceptor;
import com.alibaba.doris.dproxy.InvokeInfo;

/**
 * 
 * @author frank
 *
 */
public class ListDecompressInterceptor extends AbstractInterceptor {

    private static final Compressor compressor = new GZipCompressor();

    @Override
    public void after(InvokeInfo info) throws Throwable {

        OperationData operationData = (OperationData) info.getArgs()[0];
        Object result = operationData.getResult();
        if (result == null) {
            return;
        }

        if (!(result instanceof List)) {
            throw new IllegalArgumentException("Gotten value to be decompress must be List type.");
        }

        List<Object> resultList = (List<Object>) result;
        for (Object o : resultList) {
            if (!(o instanceof Value)) {
                throw new IllegalArgumentException("Gotten value to be decompress must be Value type.");
            }
            Value value = (Value) o;
            if (value.isCompressed()) {
                byte[] bytes = (byte[]) value.getValueBytes();
                byte[] decompressed = compressor.decompress(bytes);
                value.setValueBytes(decompressed);
            }
        }
    }
}

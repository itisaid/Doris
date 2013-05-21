/*
 * Copyright(C) 1999-2010 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.client.operation;

import java.util.List;
import java.util.Map;

import com.alibaba.doris.client.DorisClientException;

/**
 * ConnectionMetaOperation
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-4-21
 */
public interface MetaOperation {

    /**
     * put a KV pair to doris store cluster
     * 
     * @param key
     * @param value
     * @return true:success false:failed
     * @throws DorisClientException runtimeException for fatal logic error(such as: route table is null)
     */
    public boolean put(Object key, Object value) throws DorisClientException;

    /**
     * put a group of KV pairs to doris store cluster
     * 
     * @param map  a group of KV pairs
     * @throws DorisClientException runtimeException for fatal logic error(such as: route table is null)
     */
    public void puts(Map<? extends Object, ? extends Object> map) throws DorisClientException;

    /**
     * get value by key from doris store cluster
     * 
     * @param key
     * @return value
     * @throws DorisClientException runtimeException for fatal logic error(such as: route table is null)
     */
    public Object get(Object key) throws DorisClientException;

    /**
     * get a group of value list by key list from doris store cluster
     * 
     * @param keys key list
     * @return value list which index is same as key list
     * @throws DorisClientException runtimeException for fatal logic error(such as: route table is null)
     */
    public List<Object> gets(List<? extends Object> keys) throws DorisClientException;

    /**
     * get a group of KV pairs map by key list from doris store cluster
     * 
     * @param keys key list
     * @return KV pairs map which size is same as key list
     * @throws DorisClientException runtimeException for fatal logic error(such as: route table is null)
     */
    Map<Object, Object> getMap(List<? extends Object> keys) throws DorisClientException;

    /**
     * delete a KV pair from doris store cluster
     * 
     * @param key
     * @return true:success false:failed
     * @throws DorisClientException runtimeException for fatal logic error(such as: route table is null)
     */
    public boolean delete(Object key) throws DorisClientException;

    /**
     * put a KV pair to doris store cluster with bytes value,you can use this function when using customized serializer
     * 
     * @param key
     * @param value
     * @return true:success false:failed
     * @throws DorisClientException runtimeException for fatal logic error(such as: route table is null)
     * @since 0.1.4
     */
    boolean putBytes(Object key, byte[] value) throws DorisClientException;

    /**
     * get bytes value by key from doris store cluster
     * 
     * @param key
     * @return value
     * @throws DorisClientException runtimeException for fatal logic error(such as: route table is null)
     * @since 0.1.4
     */
    byte[] getBytes(Object key) throws DorisClientException;
}

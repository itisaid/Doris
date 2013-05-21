/*
 * Copyright(C) 2010-2011 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.common.data.impl;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.util.ByteUtils;

/**
 * KeyImpl
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-4
 */
public class KeyImpl implements Key {

    private int    namespace;
    private byte[] keyBytes;
    private String physicalKey;
    private byte[] physicalKeyBytes;
    private long   routeVersion;
    private int    vnode;

    /**
     * KeyImpl
     * 
     * @param namespace
     * @param key
     * @param routeVersion
     * @param vnode
     */
    public KeyImpl(int namespace, String key) {
        this(namespace, key, DEFAULT_VNODE);
    }

    /**
     * KeyImpl
     * 
     * @param namespace
     * @param key
     * @param routeVersion
     * @param vnode
     */
    public KeyImpl(int namespace, String key, int vnode) {
        this(namespace, key, 0, vnode);
    }

    /**
     * KeyImpl
     * 
     * @param namespace
     * @param key
     * @param routeVersion
     * @param vnode
     */
    public KeyImpl(int namespace, String key, long routeVersion, int vnode) {
        this.namespace = namespace;
        this.keyBytes = ByteUtils.stringToByte(key);
        this.routeVersion = routeVersion;
        buildPhysicalKey();
        this.vnode = vnode;
    }

    /**
     * @param physicalKeyBytes
     * @param vnodes
     */
    public KeyImpl(byte[] physicalKeyBytes, int vnodes) {
        this.physicalKeyBytes = physicalKeyBytes;
        this.vnode = vnodes;
    }

    public int getNamespace() {
        if (namespace <= 0) {
            if (null != physicalKeyBytes) {
                for (int i = 0; i < physicalKeyBytes.length; i++) {
                    if (SPLIT_FLAG_BYTES[0] == physicalKeyBytes[i]) {
                        namespace = Integer.valueOf(ByteUtils.byteToString(physicalKeyBytes, 0, i));
                        return namespace;
                    }
                }
            }
            // throw exception??
            return namespace;
        } else {
            return namespace;
        }
    }

    public int getVNode() {
        return vnode;
    }

    public void setVNode(int vnode) {
        this.vnode = vnode;
    }

    public String getKey() {
        if (null != keyBytes) {
            return ByteUtils.byteToString(keyBytes);
        } else {
            buildPhysicalKey();
            int pos = physicalKey.indexOf(SPLIT_FLAG);
            if (pos >= 0) {
                return physicalKey.substring(pos + 1);
            } else {
                return physicalKey;
            }
        }
    }

    public String buildPhysicalKey() {
        if (null == physicalKey) {
            if (null != physicalKeyBytes) {
                physicalKey = ByteUtils.byteToString(this.physicalKeyBytes);
            } else {
                physicalKey = String.valueOf(namespace) + SPLIT_FLAG + getKey();
                physicalKeyBytes = ByteUtils.stringToByte(physicalKey);
            }
        }

        return physicalKey;
    }

    public byte[] getPhysicalKeyBytes() {
        return physicalKeyBytes;
    }

    public long getRouteVersion() {
        return routeVersion;
    }

    public String getPhysicalKey() {
        if (null == physicalKey) {
            buildPhysicalKey();
        }
        return physicalKey;
    }

    public void parsePhysicalKey() {
        // if (null != physicalKey) {
        // int startPos = physicalKey.indexOf(SPLIT_FLAG);
        // if (startPos >= 0) {
        // this.namespace = physicalKey.substring(0, startPos);
        // this.key = physicalKey.substring(startPos + 1);
        // } else {
        // this.key = physicalKey;
        // }
        // }
    }

    public String toString() {
        return "[KeyImpl:physicalKey='" + getPhysicalKey() + "',routeVersion=" + routeVersion + ",vnode=" + vnode + "]";
    }

    public boolean equals(Object obj) {
        if (obj instanceof KeyImpl) {
            KeyImpl key0 = (KeyImpl) obj;
            return getPhysicalKey().equals(key0.getPhysicalKey());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return getPhysicalKey().hashCode();
    }

    private static final String SPLIT_FLAG       = ":";
    private static final byte[] SPLIT_FLAG_BYTES = SPLIT_FLAG.getBytes();

    public void setRouteVersion(long routeVersion) {
        this.routeVersion = routeVersion;

    }
}

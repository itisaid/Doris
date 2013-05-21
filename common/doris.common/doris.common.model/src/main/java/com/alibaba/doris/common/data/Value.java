/*
 * Copyright(C) 2010-2011 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.common.data;

import java.util.Properties;

/**
 * KeyImpl
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-4
 */
public interface Value {

    public Object getValue();

    public byte[] getValueBytes();

    public void setValueBytes(byte[] valueBytes);

    public void setValue(Object value);

    public long getTimestamp();

    public void setTimestamp(long timestamp);

    public short getFlag();

    public void setFlag(short flag);

    public CompareStatus compareVersion(Value o);
    
    public void setCompressed(boolean b);
    
    public boolean isCompressed();

    public void setProperties(Properties properties);

    public Properties getProperties();
}

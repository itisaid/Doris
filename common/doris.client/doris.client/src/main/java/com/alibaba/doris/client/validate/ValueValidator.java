/*
 * Copyright(C) 2010 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.client.validate;

import com.alibaba.doris.common.data.Value;

/**
 * @author Raymond He ( He Kun), raymond.he.kk@gmail.com
 * @since 1.0 2011-6-23
 */
public class ValueValidator implements Validator {

    private static final int MAX_VALUE_LEN = 1024 * 1024;

    /**
     * @see com.alibaba.doris.client.validate.Validator#validate(java.lang.Object)
     */
    public void validate(Object v) {
        if (v == null) {
            return;
        }
        Value value = (Value) v;

        int len = value.getValueBytes().length;

        if (len > MAX_VALUE_LEN) {
            throw new IllegalArgumentException("Invalid value, max length(compressed? " + value.isCompressed() + "):"
                                               + MAX_VALUE_LEN + "! Actual length: " + len);
        }
    }

}

/*
 * Copyright(C) 1999-2010 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.client.operation.failover.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.client.AccessException;
import com.alibaba.doris.client.net.DataSource;
import com.alibaba.doris.client.net.NetException;
import com.alibaba.doris.client.operation.failover.PeerCallback;
import com.alibaba.doris.client.operation.failover.PeerCallbackHandler;

/**
 * PeerFailoverCallbackHandler
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-4-25
 */
public class PeerFailoverCallbackHandler extends PeerBaseFailoverCallbackHandler implements PeerCallbackHandler {

    private Log        log = LogFactory.getLog(PeerFailoverCallbackHandler.class);
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public PeerCallback doPeerExecute(PeerCallback callback) throws AccessException {
        if (log.isDebugEnabled()) {
            log.debug("execute " + operationData.getOperation().getName() + " in " + dataSource.getNo() + " for "
                      + operationData.getKey());
        }

        PeerCallback pcb = null;
        boolean failed = true;
        for (int i = 1; i <= 10; i++) {
            try {
                pcb = callback.execute();
                failed = false;
                break;

            } catch (AccessException e) {
            	if( e.getCause() instanceof NetException) {
            		processAccessException(i, callback);
            	}else {
            		throw e;
            	}                
            }
        }
        if (failed) {
            throw new AccessException("Coundn't connect data server:" + callback.getDataSource());
        }
        return pcb;
    }
    
}

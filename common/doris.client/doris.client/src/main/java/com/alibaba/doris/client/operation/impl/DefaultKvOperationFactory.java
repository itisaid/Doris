/*
 * Copyright(C) 1999-2010 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.client.operation.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.client.cn.OperationDataConverter;
import com.alibaba.doris.client.operation.AbstractOperationFactory;
import com.alibaba.doris.client.operation.Operation;
import com.alibaba.doris.dproxy.ProxyFactory;

/**
 * DefaultKvOperationFactory
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-4-26
 */
public class DefaultKvOperationFactory extends AbstractOperationFactory {

    private static final String _ConfigLocation = "doris-aop.xml";
    private ProxyFactory        proxyFactory;

    public DefaultKvOperationFactory() {

        try {
            proxyFactory = new ProxyFactory(_ConfigLocation);
            Operation addOration = (Operation) proxyFactory.createObject(PutOperation.class,
                                                                         PutOperation.class.getSimpleName()
                                                                                 + "InterceptorGroup");
            addOperation(addOration.getName(), addOration);

            Operation putsOration = (Operation) proxyFactory.createObject(PutsOperation.class,
                                                                          PutsOperation.class.getSimpleName()
                                                                                  + "InterceptorGroup");
            addOperation(putsOration.getName(), putsOration);

            Operation getOperation = (Operation) proxyFactory.createObject(GetOperation.class,
                                                                           GetOperation.class.getSimpleName()
                                                                                   + "InterceptorGroup");
            this.addOperation(getOperation.getName(), getOperation);

            Operation getsOperation = (Operation) proxyFactory.createObject(GetsOperation.class,
                                                                            GetsOperation.class.getSimpleName()
                                                                                    + "InterceptorGroup");
            this.addOperation(getsOperation.getName(), getsOperation);

            Operation deleteOperation = (Operation) proxyFactory.createObject(DeleteOperation.class,
                                                                              DeleteOperation.class.getSimpleName()
                                                                                      + "InterceptorGroup");
            this.addOperation(deleteOperation.getName(), deleteOperation);

            OperationDataConverter operationDataConverter = new OperationDataConverterImpl();

            for (Map.Entry<String, Operation> entry : operationsMap.entrySet()) {
                entry.getValue().setOperationDataConverter(operationDataConverter);
            }

        } catch (InstantiationException e) {
            logger.error("Initializing Operation factory failed!", e);
        }
    }

    public void setProxyFactory(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    private static final Logger logger = LoggerFactory.getLogger(DefaultKvOperationFactory.class);
}

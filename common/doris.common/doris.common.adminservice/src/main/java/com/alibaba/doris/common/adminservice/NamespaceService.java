package com.alibaba.doris.common.adminservice;

import java.util.Map;

import com.alibaba.doris.common.Namespace;



public interface NamespaceService {

    Namespace fetchNameSpace(String nameSpace);
    Map<String,Namespace> fetchNameSpace();
}

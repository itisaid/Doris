package com.alibaba.doris.common.adminservice.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.doris.common.AdminServiceConstants;
import com.alibaba.doris.common.Namespace;
import com.alibaba.doris.common.adminservice.BaseAdminService;
import com.alibaba.doris.common.adminservice.NamespaceService;
import com.alibaba.fastjson.JSON;

public class NamespaceServiceImpl extends BaseAdminService<List<Namespace>> implements NamespaceService {

    private static NamespaceServiceImpl instance     = new NamespaceServiceImpl();
    private Map<String, Namespace>      nameSpaceMap = new ConcurrentHashMap<String, Namespace>();

    private NamespaceServiceImpl() {
        fetchNameSpace();
    }

    public static NamespaceServiceImpl getInstance() {

        return instance;
    }

    public Namespace fetchNameSpace(String nameSpace) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(AdminServiceConstants.NAME_SPACE_NAME, nameSpace);
        List<Namespace> nsList = requestForce(params);
        if (nsList != null && !nsList.isEmpty()) {
            /*if (!nameSpaceMap.isEmpty()) {
                return nameSpaceMap.get(nameSpace);
            } else {
                return null;
            }*/
            Namespace ns = nsList.get(0);
            nameSpaceMap.put(nameSpace, ns);
        }
        
        return nameSpaceMap.get(nameSpace);
    }

    public Map<String, Namespace> fetchNameSpace() {
        List<Namespace> nsList = requestForce(null);
        /*if (nsList == null || nsList.isEmpty()) {
            if (!nameSpaceMap.isEmpty()) {
                return nameSpaceMap;
            } else {
                return null;
            }
        }*/
        
        //Map<String, Namespace> map = new HashMap<String, Namespace>();
        if (nsList != null && !nsList.isEmpty()) {
            for (Namespace ns : nsList) {
                nameSpaceMap.put(ns.getName(), ns);
            }
        } else {
            nameSpaceMap.clear();
        }
        //nameSpaceMap = map;

        return nameSpaceMap;
    }

    @Override
    public List<Namespace> convert(String response) {
        return JSON.parseArray(response, Namespace.class);

    }

    @Override
    public String getActionName() {
        return AdminServiceConstants.NAME_SPACE_ACTION;
    }

}

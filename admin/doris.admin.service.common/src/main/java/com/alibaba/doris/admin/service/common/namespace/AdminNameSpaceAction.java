package com.alibaba.doris.admin.service.common.namespace;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.dataobject.NamespaceDO;
import com.alibaba.doris.admin.service.NamespaceService;
import com.alibaba.doris.admin.service.common.AdminServiceAction;
import com.alibaba.doris.common.AdminServiceConstants;
import com.alibaba.doris.common.Namespace;
import com.alibaba.fastjson.JSON;

public class AdminNameSpaceAction implements AdminServiceAction {

    
    private static final AdminNameSpaceAction instance = new AdminNameSpaceAction();
    
    private AdminNameSpaceAction(){}
    
    public static AdminNameSpaceAction getInstance() {
        return instance;
    }
    NamespaceService namespaceservice = AdminServiceLocator.getNamespaceService();

    public String execute(Map<String, String> params) {
        String nsName = params.get(AdminServiceConstants.NAME_SPACE_NAME);
        List<NamespaceDO> nsList = new ArrayList<NamespaceDO>();
        if (StringUtils.isEmpty(nsName)) {
            List<NamespaceDO> nsdList = namespaceservice.queryAllNamespaces();
            for (NamespaceDO nsd : nsdList) {
                nsList.add(nsd);
            }

        } else {
            NamespaceDO nsd = namespaceservice.queryNamespaceByName(nsName);

            if (nsd != null) {
                nsList.add(nsd);
            }
        }
        return JSON.toJSONString(nsList);//service 用NamespaceDO 序列化，client用Namespace反序列化

    }

    private Namespace convert(NamespaceDO nsd) {
        Namespace ns = new Namespace();
        ns.setClassName(nsd.getClassName());
        ns.setCompressMode(nsd.getCompressMode());
        ns.setCompressThreshold(nsd.getCompressThreshold());
        ns.setCopyCount(nsd.getCopyCount());
        ns.setFirstOwner(nsd.getFirstOwner());
        ns.setName(nsd.getName());
        ns.setRemark(nsd.getRemark());
        ns.setSecondOwner(nsd.getSecondOwner());
        ns.setSerializeMode(nsd.getSerializeMode());
        ns.setStatus(nsd.getStatus());
        return ns;
    }

}

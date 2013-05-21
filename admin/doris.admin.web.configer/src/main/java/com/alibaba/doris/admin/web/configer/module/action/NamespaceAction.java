package com.alibaba.doris.admin.web.configer.module.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.alibaba.citrus.service.form.CustomErrors;
import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.Navigator;
import com.alibaba.citrus.turbine.dataresolver.FormField;
import com.alibaba.citrus.turbine.dataresolver.FormGroup;
import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.dataobject.NamespaceDO;
import com.alibaba.doris.admin.service.AdminService;
import com.alibaba.doris.admin.service.NamespaceService;
import com.alibaba.doris.admin.web.configer.util.WebConstant;

/**
 * @project:Doris
 * @author : len.liu
 * @datetime : 2011-5-14 下午10:28:23
 * @version :0.1
 * @Modification:
 */
public class NamespaceAction {

    NamespaceService namespaceService = AdminServiceLocator.getNamespaceService();

    public void doAddNamespace(
                               @FormGroup("namespaceForm") NamespaceDO namespaceDO,
                               @FormField(name = "nameRepeatedError", group = "namespaceForm") CustomErrors nameRepeatedError,
                               Navigator nav) {
        NamespaceDO namespaceDoFromDb = namespaceService.queryNamespaceByName(namespaceDO.getName());
        if (namespaceDoFromDb != null) {
            nameRepeatedError.setMessage("nameRepeated");
            return;
        }
        namespaceDO.setStatus(1);
        namespaceService.addNamespace(namespaceDO);
        nav.redirectTo(WebConstant.NAMESPACE_LIST_LINK);

    }

    public void doRemoveNamespace(Context context, Navigator nav, HttpServletRequest request) {
        String name = request.getParameter("name");
        if (!StringUtils.isBlank(name)) {
            namespaceService.deleteNamespaceByName(name);
        }
        context.put("message", "删除成功!");
    }

    public void doQueryNamespaces(@FormGroup("namespaceForm") NamespaceDO namespaceDO, Navigator nav) {

        List<NamespaceDO> namespaceList = namespaceService.queryAllNamespaces();
    }

}

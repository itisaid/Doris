package com.alibaba.doris.admin.web.user.module.action;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.service.form.CustomErrors;
import com.alibaba.citrus.turbine.Navigator;
import com.alibaba.citrus.turbine.dataresolver.FormField;
import com.alibaba.citrus.turbine.dataresolver.FormGroup;
import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.dataobject.UserDO;
import com.alibaba.doris.admin.service.UserService;
import com.alibaba.doris.admin.web.configer.util.WebConstant;

/**
 * @project :Doris
 * @author : len.liu
 * @datetime : 2011-6-9 下午05:34:07
 * @version :0.1
 * @Modification:
 */
public class LoginAction {
    @Autowired
    private HttpSession session;
    private UserService userService = AdminServiceLocator.getUserService();

    public void doLogin(@FormGroup("loginForm") UserDO userDO,
                        @FormField(name = "nameIsNotExsitError", group = "loginForm") CustomErrors nameIsNotExsitError,
                        @FormField(name = "passwdError", group = "loginForm") CustomErrors passwdError,
                        Navigator nav) {
        UserDO userDOFromDb = userService.findUserByName(userDO.getName());
        // 用户不存在
        if (userDOFromDb == null) {
            nameIsNotExsitError.setMessage("nameIsNotExsit");
            return;
        }
        // 用户存在但是密码不对
        if (!userDOFromDb.getPassword().equals(userDO.getPassword())) {
            passwdError.setMessage("passwdError");
            return;
        }
        userDO.setLogined(true);
        session.setAttribute(WebConstant.DORIS_USER_SESSION_KEY, userDO);
        nav.redirectTo(WebConstant.INDEX_LINK);
    }
    

    /**
     * Log out.
     * 
     * @param context
     * @param navigator
     * @throws Exception
     */
    public void doLogout(Navigator navigator) {
        session.removeAttribute(WebConstant.DORIS_USER_SESSION_KEY);
        // 跳转到登录页面
        navigator.redirectTo(WebConstant.LOGIN_LINK);
    }
}

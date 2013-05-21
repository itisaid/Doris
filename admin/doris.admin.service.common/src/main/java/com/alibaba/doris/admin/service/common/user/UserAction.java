package com.alibaba.doris.admin.service.common.user;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.dataobject.UserDO;
import com.alibaba.doris.admin.service.UserService;
import com.alibaba.doris.admin.service.common.AdminServiceAction;
import com.alibaba.doris.common.AdminServiceConstants;

public class UserAction implements AdminServiceAction {

	private static final Logger logger = LoggerFactory.getLogger( UserAction.class );
	
    private UserAction() {

    }

    private static final UserAction instance = new UserAction();

    public static UserAction getInstance() {
        return instance;
    }

    UserService adminService = AdminServiceLocator.getUserService();

    public String execute(Map<String, String> params) {
        String userName = params.get(AdminServiceConstants.USER_AUTH_USER_NAME);
        String password = params.get(AdminServiceConstants.USER_AUTH_PASSWORD);
        
        if(logger.isDebugEnabled()) {
        	logger.info( String.format("User auth request - username:%s, password:%s",userName,password));
        }
        UserDO userDO = adminService.findUserByName(userName);
      
        if (userDO != null && userDO.getPassword().equals(password)) {
        	
        	 if(logger.isDebugEnabled()) {
             	logger.info( String.format("User auth result - privilege:%d",userDO.getPrivilege()));
             }
            return String.valueOf(userDO.getPrivilege());
        } else {
        	 if(logger.isDebugEnabled()) {
              	logger.info( String.format("User auth fail, user not exist - %s", userName ));
              }
            return "-1";
        }
    }
}

package com.alibaba.doris.admin.web.configer.util;

/**
 * @project :Doris
 * @author : len.liu
 * @datetime : 2011-6-2 下午04:54:00
 * @version :0.1
 * @Modification:
 */
public interface WebConstant {

    /**
     * Redirect Link
     */
    public String NODE_ADD_LINK               = "";

    public String NODE_MANAGE_LIST_LINK       = "nodeManage";

    public String NAMESPACE_ADD_LINK          = "";

    public String NAMESPACE_LIST_LINK         = "namespaceListLink";

    public String MONITOR_LINK                = "";

    public String NEW_NODES_PREVIEW_LINK      = "newNodesPreviewLink";
    
    public String NODE_EDIT_LINK              = "nodeEdit";

    public String INDEX_LINK                  = "indexLink";

    public String LOGIN_LINK                  = "loginLink";

    /**
     * Context Key
     */
    public String NO_MIGRATE_SEQUENCE_IDS_KEY = "noMigrateSequences";

    public String ALL_SEQUENCE_IDS_KEY        = "allSequences";

    public String DORIS_USER_SESSION_KEY      = "loginUser";

    public String LOGIN_PAGE                  = "/user/login.htm";

    /** Login页面返回URL的key。 */
    public String LOGIN_RETURN_KEY            = "return";

    /** Petstore后台的ACL realm。 */
    public String ACCESS_ADMIN_REALM          = "admin";

    public int    DEFAULT_ITEMS_PER_PAGE      = 36;

}

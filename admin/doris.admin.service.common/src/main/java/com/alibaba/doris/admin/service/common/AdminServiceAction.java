package com.alibaba.doris.admin.service.common;

import java.util.Map;

/**
 * @author frank
 */
public interface AdminServiceAction {

    String execute(Map<String, String> params);
}

package com.alibaba.doris.dataserver.core;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface RequestFilter {

    void doFilter(Request request, Response response, RequestFilterChian filterChain);
}

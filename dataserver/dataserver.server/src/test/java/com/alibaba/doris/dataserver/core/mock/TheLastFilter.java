package com.alibaba.doris.dataserver.core.mock;

import com.alibaba.doris.dataserver.core.Request;
import com.alibaba.doris.dataserver.core.RequestFilter;
import com.alibaba.doris.dataserver.core.RequestFilterChian;
import com.alibaba.doris.dataserver.core.Response;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class TheLastFilter implements RequestFilter {

    public void doFilter(Request request, Response response, RequestFilterChian filterChain) {
        System.out.println("TheLastFilter");
        filterChain.doFilter(request, response);
    }

}

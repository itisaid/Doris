package com.alibaba.doris.dataserver.core;

import junit.framework.TestCase;

import com.alibaba.doris.common.router.virtual.VirtualRouterImpl;
import com.alibaba.doris.dataserver.core.mock.MockRequest;
import com.alibaba.doris.dataserver.core.mock.MockResponse;
import com.alibaba.doris.dataserver.core.mock.NormalFilter1;
import com.alibaba.doris.dataserver.core.mock.NormalFilter2;
import com.alibaba.doris.dataserver.core.mock.TheFirstFilter;
import com.alibaba.doris.dataserver.core.mock.TheLastFilter;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class RequestFilterChianTest extends TestCase {

    {
        VirtualRouterImpl.setDebug(true);
    }

    public void testRequestFilterChainFactory() {
        RequestFilterChainFactory factory = new RequestFilterChainFactory();
        RequestFilterChian filterChain = factory.getFilterChian();
        assertNotNull(filterChain);
    }

    public void testExecuteRequestFilterChain() {
        RequestFilterChainFactory factory = new RequestFilterChainFactory();
        RequestFilterChian filterChain = factory.getFilterChian();
        Request request = new MockRequest(null);
        Response response = new MockResponse();

        RequestFilter filter = new RequestFilter() {

            public void doFilter(Request request, Response response, RequestFilterChian filterChain) {
                assertTrue(filterChain != null);
                filterChain.doFilter(request, response);
            }
        };

        try {
            filterChain = new DefaultRequestFilterChian();
            ((DefaultRequestFilterChian) filterChain).addFilter(filter);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testAddFilter() {
        RequestFilterChainFactory factory = new RequestFilterChainFactory();
        RequestFilterChian filterChain = factory.getFilterChian();
        RequestFilter first = new TheFirstFilter();
        RequestFilter last = new TheLastFilter();
        filterChain.addFirstFilter(first);
        filterChain.addLastFilter(last);

        RequestFilter normal1 = new NormalFilter1();
        filterChain.addFilterAfter(first, normal1);

        Request request = new MockRequest(null);
        Response response = new MockResponse();
        filterChain.doFilter(request, response);

        System.out.println("-------------------------------");

        try {
            filterChain.addFirstFilter(first);
            fail();
        } catch (Exception e) {
            ;
        }

        try {
            filterChain.addLastFilter(last);
            fail();
        } catch (Exception e) {
            ;
        }

        try {
            filterChain.addFilterAfter(last, new NormalFilter2());
            fail();
        } catch (Exception e) {
            ;
        }

        filterChain.addFilterAfter(normal1, new NormalFilter2());
        request = new MockRequest(null);
        response = new MockResponse();
        filterChain.doFilter(request, response);
    }
}

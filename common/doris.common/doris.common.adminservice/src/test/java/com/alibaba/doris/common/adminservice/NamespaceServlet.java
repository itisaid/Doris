package com.alibaba.doris.common.adminservice;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.doris.common.AdminServiceConstants;
import com.alibaba.fastjson.JSON;

public class NamespaceServlet extends HttpServlet {

    /**
     *  
     */
    private static final long serialVersionUID = -5446749189586775818L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Map<String, Object> result = new HashMap<String, Object>();
        result.put(AdminServiceConstants.NAME_SPACE_ACTION, Boolean.TRUE);
        PrintWriter writer = response.getWriter();
        writer.write(JSON.toJSONString(result));

        writer.close();

    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}

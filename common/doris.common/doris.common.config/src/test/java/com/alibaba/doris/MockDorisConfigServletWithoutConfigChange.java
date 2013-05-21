package com.alibaba.doris;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

public class MockDorisConfigServletWithoutConfigChange extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = -5446749189586775818L;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {
        Map<String, String> map = new HashMap<String, String>();
        resp.getWriter().write(JSON.toJSONString(map));

    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}

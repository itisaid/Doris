package com.alibaba.doris;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.doris.common.NodeRouteStatus;
import com.alibaba.doris.common.RouteConfigInstance;
import com.alibaba.doris.common.StoreNode;
import com.alibaba.doris.common.StoreNodeSequenceEnum;
import com.alibaba.fastjson.JSON;

public class MockDorisConfigServletWithConfigChange extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = -5446749189586775818L;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {
        StoreNode node = new StoreNode();
        node.setIp("127.0.0.1");
        node.setLogicId(1);
        node.setPhId("phId");
        node.setPort(8081);
        node.setSequence(StoreNodeSequenceEnum.NORMAL_SEQUENCE_1);
        node.setStatus(NodeRouteStatus.OK);
        node.setURL("http://localhos/data");

        List<StoreNode> nodeLists = new ArrayList<StoreNode>();
        nodeLists.add(node);
        RouteConfigInstance routeConfig = new RouteConfigInstance();
        routeConfig.setVersion(1111);
        routeConfig.setStoreNodes(nodeLists);
        
        Map<String, String> map = new HashMap<String, String>();
        map.put("routeConfig", JSON.toJSONString(routeConfig));
        String respJson = JSON.toJSONString(map);
        resp.getWriter().write(respJson);

    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}

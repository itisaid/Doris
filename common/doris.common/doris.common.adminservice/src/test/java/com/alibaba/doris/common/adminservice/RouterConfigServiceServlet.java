package com.alibaba.doris.common.adminservice;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.alibaba.doris.common.NodeRouteStatus;
import com.alibaba.doris.common.RouteConfigInstance;
import com.alibaba.doris.common.StoreNode;
import com.alibaba.doris.common.StoreNodeSequenceEnum;
import com.alibaba.fastjson.JSON;

public class RouterConfigServiceServlet extends HttpServlet {

    /**
     *  
     */
    private static final long serialVersionUID = -5446749189586775818L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String actionName = request.getParameter("actionName");
        String[] actions = StringUtils.split(actionName, ",");
        String resTxt = StringUtils.EMPTY;
        if (actionName != null && actionName.length() > 0) {
            String configVersion = request.getParameter("configVersion");
            String[] configVersions = StringUtils.split(configVersion, ",");
            boolean noVersion = false;
            if (configVersions == null || actions.length != configVersions.length) {
                noVersion = true;
            }
            Map<String, String> resultMap = new HashMap<String, String>();
            for (int i = 0; i < actions.length; i++) {
                String config = mockLoadConfig(actions[i],
                        noVersion ? 0L : Long.parseLong(configVersions[i]));
                resultMap.put(actions[i], config);
            }

            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=UTF-8");
            resTxt = JSON.toJSONString(resultMap);

        } else {
            resTxt = JSON.toJSONString(new HashMap<String, String>());
        }
        PrintWriter writer = response.getWriter();
        writer.write(resTxt);

        writer.close();

    }
    
    private String mockLoadConfig(String action, Long version) {
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
        
        if (version >= 1111) {
            return null;
        } 
        return JSON.toJSONString(routeConfig);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}

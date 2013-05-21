package com.alibaba.doris.dataserver.monitor.action;

import com.alibaba.doris.common.RealtimeInfo;
import com.alibaba.doris.common.data.util.ByteUtils;
import com.alibaba.doris.dataserver.action.data.ActionData;
import com.alibaba.doris.dataserver.action.parser.BaseActionParser;
import com.alibaba.doris.dataserver.monitor.support.RealtimeInfoBuilder;
import com.alibaba.doris.dataserver.net.ByteBufferWrapper;
import com.alibaba.doris.dataserver.net.protocol.text.ProtocolConstant;
import com.alibaba.fastjson.JSON;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class StatsActionParser extends BaseActionParser {

    /**
     * 输入
     */
    public ActionData readHead(byte[] header, int startPos) {

        StatsActionData md = new StatsActionData();
        int[] pos = { startPos };
        md.setViewType(parseNextField(header, pos));
        md.setNamespace(parseNextField(header, pos));
        //
        return new StatsActionData();
    }

    /**
     * 输出
     */
    public void writeHead(ByteBufferWrapper buffer, ActionData actionData) {
        RealtimeInfoBuilder build = new RealtimeInfoBuilder((StatsActionData) actionData);

        RealtimeInfo realtimeInfo = build.buildRealtimeInfo();

        buffer.writeBytes(ByteUtils.stringToByte(JSON.toJSONString(realtimeInfo)));
        buffer.writeBytes(ProtocolConstant.CRLF);
        buffer.writeBytes(ProtocolConstant.END);
        buffer.writeBytes(ProtocolConstant.CRLF);
    }

}

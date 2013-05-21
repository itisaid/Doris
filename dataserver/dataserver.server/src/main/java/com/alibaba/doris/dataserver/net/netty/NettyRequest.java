package com.alibaba.doris.dataserver.net.netty;

import java.net.InetSocketAddress;

import org.jboss.netty.channel.ChannelHandlerContext;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.KeyFactory;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.ValueFactory;
import com.alibaba.doris.dataserver.ApplicationContext;
import com.alibaba.doris.dataserver.action.data.ActionData;
import com.alibaba.doris.dataserver.action.data.BaseActionData;
import com.alibaba.doris.dataserver.action.data.CommonActionData;
import com.alibaba.doris.dataserver.core.BaseRequest;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class NettyRequest extends BaseRequest {

    public NettyRequest(ApplicationContext appContext, ActionData md, ChannelHandlerContext ctx) {
        super(appContext, md);
        this.ctx = ctx;
    }

    public String getClientAddress() {
        InetSocketAddress localAddress = (InetSocketAddress) ctx.getChannel().getRemoteAddress();
        return localAddress.getAddress().getHostAddress();
    }

    @Override
    public int getClientPort() {
        InetSocketAddress localAddress = (InetSocketAddress) ctx.getChannel().getRemoteAddress();
        return localAddress.getPort();
    }

    @Override
    public int getServerPort() {
        InetSocketAddress localAddress = (InetSocketAddress) ctx.getChannel().getLocalAddress();
        return localAddress.getPort();
    }

    public String getServerAddress() {
        InetSocketAddress localAddress = (InetSocketAddress) ctx.getChannel().getLocalAddress();
        return localAddress.getAddress().getHostName();
    }

    public Key getKey() {
        if (null != key) {
            return key;
        }

        ActionData ad = super.getActionData();
        if (ad instanceof BaseActionData) {
            BaseActionData actionData = (BaseActionData) ad;
            key = KeyFactory.createKey(actionData.getKeyBytes(), actionData.getVnode());
            key.setRouteVersion(actionData.getRouteVersion());
            return key;
        }

        return null;
    }

    public Value getValue() {
        if (null != this.value) {
            return this.value;
        }

        ActionData ad = super.getActionData();
        if (ad instanceof CommonActionData) {
            CommonActionData md = (CommonActionData) ad;
            this.value = ValueFactory.createValue(md.getBodyByteArray(), md.getFlag(), md.getTimestamp());
            return value;
        }

        return null;
    }

    private Value                 value;
    private Key                   key;
    private ChannelHandlerContext ctx;

}

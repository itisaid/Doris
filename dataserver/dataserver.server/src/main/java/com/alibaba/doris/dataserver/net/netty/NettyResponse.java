package com.alibaba.doris.dataserver.net.netty;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;

import com.alibaba.doris.dataserver.action.Action;
import com.alibaba.doris.dataserver.action.ActionFactory;
import com.alibaba.doris.dataserver.action.data.ActionData;
import com.alibaba.doris.dataserver.action.data.ErrorActionData;
import com.alibaba.doris.dataserver.core.BaseResponse;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class NettyResponse extends BaseResponse {

    public NettyResponse(MessageEvent e, ActionData oriActionData) {
        this.orignalActionData = oriActionData;
        this.e = e;
    }

    public void write(ActionData md) {
        this.md = md;
    }

    public void flush() {
        Channel channel = e.getChannel();
        if (channel.isConnected()) {
            if (null != md) {
                e.getChannel().write(md);
            } else {
                Action action = ActionFactory.getAction(orignalActionData.getActionType());
                errorAd.setErrorMessage("Couldn't find any action data to flush. You should call the write method of response.["
                                        + action.getClass().getName() + "]");
                e.getChannel().write(errorAd);
            }
        } else {
            // TODO: When connection is closed by client. Should we do something here?
        }
    }

    public void close() {
        ChannelFuture future = Channels.close(e.getChannel());
        future.awaitUninterruptibly();
    }

    private ActionData      md;
    private ActionData      orignalActionData;
    private MessageEvent    e;
    private ErrorActionData errorAd = new ErrorActionData(ErrorActionData.SERVER_ERROR);
}

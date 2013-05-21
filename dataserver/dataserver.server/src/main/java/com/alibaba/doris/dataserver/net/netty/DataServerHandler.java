package com.alibaba.doris.dataserver.net.netty;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.dataserver.ApplicationContext;
import com.alibaba.doris.dataserver.action.ActionExecuteException;
import com.alibaba.doris.dataserver.action.data.ActionData;
import com.alibaba.doris.dataserver.action.data.ErrorActionData;
import com.alibaba.doris.dataserver.config.ModuleConstances;
import com.alibaba.doris.dataserver.core.Request;
import com.alibaba.doris.dataserver.core.RequestFilterChian;
import com.alibaba.doris.dataserver.core.Response;
import com.alibaba.doris.dataserver.net.NetworkModule;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DataServerHandler extends SimpleChannelUpstreamHandler {

    public DataServerHandler(ApplicationContext appContext) {
        this.appContext = appContext;
        this.networkModule = (NetworkModule) appContext.getModuleByName(ModuleConstances.NETWORK_MODULE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        // super.exceptionCaught(ctx, e);
        logger.error("DataServerHandler catch exception, the client IP address is:"
                     + ctx.getChannel().getRemoteAddress(), e.getCause());
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object message = e.getMessage();
        if (message != null && message instanceof ActionData) {
            dispatch(ctx, e, (ActionData) message);
        } else {
            super.messageReceived(ctx, e);
        }
    }

    private Response dispatch(ChannelHandlerContext ctx, MessageEvent messageEvent, ActionData md) {
        Request request = new NettyRequest(appContext, md, ctx);
        NettyResponse response = new NettyResponse(messageEvent, md);
        try {
            requestFilterChian = networkModule.getRequestFilterChain();
            requestFilterChian.doFilter(request, response);
        } catch (ActionExecuteException actException) {
            logger.error("doFilter: ", actException);
            // action execute failed
            ErrorActionData ad = new ErrorActionData(ErrorActionData.SERVER_ERROR);
            // String msg = actException.getMessage();
            // if (msg != null) {
            // ad.setErrorMessage("Execute action [" + actException.getActionType().getName()
            // + "] failed. Error message:" + actException.getMessage());
            // }
            response.write(ad);
        } catch (Throwable e) {
            logger.error("doFilter: ", e);
            ErrorActionData ad = new ErrorActionData(ErrorActionData.SERVER_ERROR);
            String msg = e.getMessage();
            if (msg != null) {
                ad.setErrorMessage(msg);
            }

            response.write(ad);
        }

        if (response != null) {
            try {
                response.flush();
            } catch (Throwable e) {
                logger.error("Flush data failed.", e);
            }
        }

        return response;
    }

    private ApplicationContext  appContext;
    private NetworkModule       networkModule;
    private RequestFilterChian  requestFilterChian;
    private static final Logger logger = LoggerFactory.getLogger(DataServerHandler.class);
}

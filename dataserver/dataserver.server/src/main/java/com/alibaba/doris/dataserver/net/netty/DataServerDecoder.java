package com.alibaba.doris.dataserver.net.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.dataserver.action.data.ActionData;
import com.alibaba.doris.dataserver.action.data.ErrorActionData;
import com.alibaba.doris.dataserver.action.data.SupportBodyActionData;
import com.alibaba.doris.dataserver.action.parser.ActionParser;
import com.alibaba.doris.dataserver.net.InvalidCommandException;
import com.alibaba.doris.dataserver.net.protocol.ProtocolParseExcetion;
import com.alibaba.doris.dataserver.net.protocol.text.TextProtocolDecoder;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DataServerDecoder extends FrameDecoder {

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        if (actionData == null) {
            boolean breakLoop = true;
            ErrorActionData errorActionData = null;
            do {
                try {
                    buffer.markReaderIndex();
                    actionData = decoder.readHeader(buffer);
                    if (actionData == null) {
                        // 如果当前数据不是一个完整的命令，则将流中的数据指针复原到起始位置。
                        buffer.resetReaderIndex();
                        return errorActionData;
                    }

                    if (null != errorActionData) {
                        // 将流中的数据指针复原到第一个有效命令序列的起始位置。
                        buffer.resetReaderIndex();
                        breakLoop = true;
                        // reset actionData, It is important.
                        actionData = null;
                        return errorActionData;
                    }
                } catch (ProtocolParseExcetion e) {
                    // 处理无法识别的命令序列：读下一个可识别的命令,
                    // 通过breakLoop一直循环下去，要么读到正确的命令包，
                    // 要么流中的数据读完。
                    breakLoop = false;
                    logger.error("Decode command error.", e);
                    if (null == errorActionData) {
                        if (e instanceof InvalidCommandException) {
                            errorActionData = new ErrorActionData(ErrorActionData.UNKNOWN_COMMAND);
                            errorActionData.setErrorMessage(e.getMessage());
                        } else {
                            errorActionData = new ErrorActionData(ErrorActionData.CLIENT_ERROR);
                        }
                    }
                } catch (Exception e) {
                    breakLoop = false;
                    logger.error("Decode command error.", e);
                    if (null == errorActionData) {
                        errorActionData = new ErrorActionData(ErrorActionData.CLIENT_ERROR);
                        errorActionData.setErrorMessage(e.getMessage());
                    }
                }
            } while (!breakLoop);
        }

        if (actionData instanceof SupportBodyActionData) {
            SupportBodyActionData contentMetaData = (SupportBodyActionData) actionData;
            if (contentMetaData.isNeedReadBody()) {
                try {
                    buffer.markReaderIndex();
                    byte[] bodyByteArray = decoder.readBody(buffer, contentMetaData);
                    if (bodyByteArray == null) {
                        buffer.resetReaderIndex();
                        if (logger.isDebugEnabled()) {
                            logger.debug("Unfinished command : buffer size:" + buffer.readableBytes() + " Command:"
                                         + actionData);
                        }
                        return null;
                    }
                    ActionParser parser = actionData.getActionType().getParser();
                    parser.readBody(bodyByteArray, contentMetaData);
                } catch (Exception e) {
                    ErrorActionData errorActionData = new ErrorActionData(ErrorActionData.CLIENT_ERROR);
                    errorActionData.setErrorMessage(e.getMessage());
                    actionData = null;// It is important for actionData to be set null;
                    return errorActionData;
                }
            }
        }

        ActionData result = actionData;
        actionData = null;
        if (logger.isDebugEnabled() && channel != null) {
            logger.debug("client:" + channel.getRemoteAddress() + " Server received command:" + result + " thread:"
                         + Thread.currentThread().getName());
        }
        return result;
    }

    private ActionData                 actionData;
    private static TextProtocolDecoder decoder = new TextProtocolDecoder();
    private static final Logger        logger  = LoggerFactory.getLogger(DataServerDecoder.class);
}

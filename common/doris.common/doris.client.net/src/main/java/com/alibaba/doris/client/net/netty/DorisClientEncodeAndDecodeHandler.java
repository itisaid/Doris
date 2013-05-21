package com.alibaba.doris.client.net.netty;

import static org.jboss.netty.channel.Channels.write;

import java.io.IOException;
import java.nio.channels.NotYetConnectedException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.client.net.command.BaseCommand;
import com.alibaba.doris.client.net.command.CheckQueueCommand;
import com.alibaba.doris.client.net.command.Command;
import com.alibaba.doris.client.net.command.ErrorType;
import com.alibaba.doris.client.net.protocol.ProtocolParser;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DorisClientEncodeAndDecodeHandler extends FrameDecoder implements ChannelDownstreamHandler {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        Throwable t = e.getCause();
        if (t instanceof IOException) {
            processChannelClosed(ctx);
        } else if (t instanceof NotYetConnectedException) {
            processChannelClosed(ctx);
        }

        super.exceptionCaught(ctx, e);
    }

    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        // Encode
        if (!(e instanceof MessageEvent)) {
            ctx.sendDownstream(e);
            return;
        }

        MessageEvent evt = (MessageEvent) e;

        Command<?> command = (Command<?>) evt.getMessage();

        if (needCheckCommandQueue) {
            // 插入一条check命令；
            writeCommand(ctx, evt, new CheckQueueCommand());
            needCheckCommandQueue = false;
            logger.error("Send CheckQueueCommand. to channel:" + ctx.getChannel());
        }

        writeCommand(ctx, evt, command);
    }

    private void writeCommand(ChannelHandlerContext ctx, MessageEvent evt, Command<?> command) throws Exception {
        Object encodedMessage = encode(ctx, evt.getChannel(), command);
        if (command == encodedMessage) {
            ctx.sendDownstream(evt);
        } else if (encodedMessage != null) {
            try {
                boolean closeChannel = false;
                ChannelFuture feture = evt.getFuture();

                write(ctx, feture, encodedMessage, evt.getRemoteAddress());

                // 等待60s超时
                if (feture.await(60000)) {
                    if (feture.isSuccess()) {
                        writeFailureCount = 0;
                        commandQueue.offer(command);
                        if (logger.isDebugEnabled()) {
                            logger.debug("Send command:" + command + " to " + ctx.getChannel().getRemoteAddress());
                        }
                        return;
                    }

                    writeFailureCount++;
                    if (writeFailureCount > MAX_WRITE_FAILURE_TIMES) {
                        // 如果连续多次写入数据失败，强制关闭通道；
                        closeChannel = true;
                    }
                } else {
                    // 如果等待60s还无法写入数据则强制关闭通道；
                    closeChannel = true;
                }

                if (command instanceof BaseCommand<?>) {
                    BaseCommand<?> baseCommand = (BaseCommand<?>) command;
                    baseCommand.setErrorMessage("Send command failed!");
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("Send command failed:" + command);
                }

                command.complete();

                if (closeChannel) {
                    Channel channel = ctx.getChannel();
                    if (channel.isOpen()) {
                        logger.error("Couldn't sent command to:" + channel + "; close channel;");
                        // 如果连续往通道写数据失败；则尝试关闭通道；
                        Channels.close(channel).awaitUninterruptibly(1000);
                    }
                }
            } catch (Exception exception) {
                if (command instanceof BaseCommand<?>) {
                    BaseCommand<?> baseCommand = (BaseCommand<?>) command;
                    baseCommand.setErrorMessage("Send command failed!");
                }

                // 往通道写入数据失败需要通知命令写入数据失败，并从等待结果队列移除命令；
                writeFailureCount++;
                command.complete();
                logger.error("Send command failed:" + command);
                throw exception;
            }
        }
    }

    private long writeFailureCount       = 0;
    private int  MAX_WRITE_FAILURE_TIMES = 10;

    protected Object encode(ChannelHandlerContext ctx, Channel channel, Command<?> command) throws Exception {

        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer(channel.getConfig().getBufferFactory());
        ProtocolParser parser = command.getProtocolParser();

        try {
            parser.encode(command, buffer);
            // if (logger.isDebugEnabled()) {
            // logger.debug("Encoding command:" + command);
            // }
        } catch (Exception e) {
            if (command instanceof BaseCommand<?>) {
                BaseCommand<?> baseCommand = (BaseCommand<?>) command;
                baseCommand.setErrorMessage("Encoding command failed!");
            }
            command.complete();
            throw e;
        }

        return buffer;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        if (!buffer.readable()) {
            return null;
        }

        if (null == currentCommand) {
            // 防止阻塞netty的worker线程；
            currentCommand = commandQueue.poll(100, TimeUnit.MILLISECONDS);
            if (null == currentCommand) {
                logger.error("Failed to receive command from queue. (The queue size is 0.) channel:" + channel
                             + " buffer.readableBytes:" + buffer.readableBytes());
                checkCommandQueueIsOk();
                return null;
            }
        }

        ProtocolParser parser = currentCommand.getProtocolParser();
        try {
            if (parser.decode(currentCommand, buffer)) {
                Command<?> rCommand = currentCommand;
                currentCommand = null;
                if (logger.isDebugEnabled()) {
                    logger.debug("Received command:" + rCommand + " from " + channel + " queue size:"
                                 + commandQueue.size());
                }

                appendExtraErrorInformationForFailure(channel, rCommand);

                if (rCommand instanceof CheckQueueCommand) {
                    // 检查通道命令直接忽略；
                    logger.error("Received checkQueueCommand: from " + channel + "queue size:" + commandQueue.size()
                                 + ", skip bytes:" + ((CheckQueueCommand) rCommand).getDiscardBytes());
                    return null;
                }

                return rCommand;
            }
        } catch (Throwable e) {
            logger.error("Received command failed:" + currentCommand + " from " + channel, e);
            // 解析当前命令数据出错，直接返回出错的命令。TODO流中没处理完的数据如何处理？
            if (currentCommand != null) {
                Command<?> rCommand = currentCommand;
                currentCommand = null;

                appendExtraErrorInformationForFailure(channel, rCommand);
                return rCommand;
            }
        }

        return null;
    }

    private void appendExtraErrorInformationForFailure(Channel channel, Command<?> rCommand) {
        if (rCommand instanceof BaseCommand) {
            BaseCommand<?> baseCommand = (BaseCommand<?>) rCommand;
            if (!baseCommand.isSuccess() && ErrorType.VERSION_OUT_OF_DATE != baseCommand.getErrorType()) {
                baseCommand.setErrorMessage(baseCommand.getErrorMessage() + " IP:" + channel.getRemoteAddress());
            }
        }
    }

    private void checkCommandQueueIsOk() {
        long currentTime = System.currentTimeMillis();
        if (lastEmptyQueueTime == 0) {
            lastEmptyQueueTime = currentTime;
        }

        this.timesOfFetchEmptyQueue++;

        // 如果怀疑队列中的数据存在问题，通知插入检查通道是否正常的命令；
        if ((currentTime - lastEmptyQueueTime) <= MAX_STATISTIC_INTERVAL) {
            if (timesOfFetchEmptyQueue >= MAX_FETCH_EMPTY_COMMAND_TIMES) {
                needCheckCommandQueue = true;
                lastEmptyQueueTime = 0;
                timesOfFetchEmptyQueue = 0;
                logger.error("To notify send CheckQueueCommand.");
            }
        }
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        processChannelClosed(ctx);
        super.channelClosed(ctx, e);
    }

    private void processChannelClosed(ChannelHandlerContext ctx) {
        // channel关闭时，需要清空队列中剩余的数据。 current command也要处理？？
        Command<?> command = null;
        if (null != currentCommand) {
            notifyCommandThatChannelHasClosed(ctx, currentCommand);
            currentCommand = null;
        }

        while ((command = commandQueue.poll()) != null) {
            notifyCommandThatChannelHasClosed(ctx, command);
        }
    }

    private void notifyCommandThatChannelHasClosed(ChannelHandlerContext ctx, Command<?> command) {
        if (command instanceof BaseCommand<?>) {
            BaseCommand<?> baseCommand = (BaseCommand<?>) command;
            baseCommand.setSuccess(false);
            baseCommand.setErrorMessage("Connection closed! remoteAddress:" + ctx.getChannel().getRemoteAddress());
            baseCommand.setErrorType(ErrorType.CONNECTION);
        }
        command.complete();
    }

    private final static long         MAX_STATISTIC_INTERVAL        = 60 * 1000;                                                       // 60
    private final static int          MAX_FETCH_EMPTY_COMMAND_TIMES = 1;                                                               // 设置成1退化成每次没有接收到数据都插入一条check命令；
    // seconds;
    private long                      lastEmptyQueueTime;
    private int                       timesOfFetchEmptyQueue;
    private volatile boolean          needCheckCommandQueue         = false;
    private Command<?>                currentCommand;
    private BlockingQueue<Command<?>> commandQueue                  = new ArrayBlockingQueue<Command<?>>(5000);
    private static final Logger       logger                        = LoggerFactory.getLogger(DorisClientEncodeAndDecodeHandler.class);
}

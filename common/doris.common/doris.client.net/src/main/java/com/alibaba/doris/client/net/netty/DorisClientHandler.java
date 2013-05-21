package com.alibaba.doris.client.net.netty;

import java.net.ConnectException;
import java.util.Map;
import java.util.Set;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.client.net.ConnectionMetaOperation;
import com.alibaba.doris.client.net.OperationFuture;
import com.alibaba.doris.client.net.command.CheckCommand;
import com.alibaba.doris.client.net.command.Command;
import com.alibaba.doris.client.net.command.CompareAndDeleteCommand;
import com.alibaba.doris.client.net.command.CompareAndSetCommand;
import com.alibaba.doris.client.net.command.DeleteCommand;
import com.alibaba.doris.client.net.command.GetCommand;
import com.alibaba.doris.client.net.command.MigrateCommand;
import com.alibaba.doris.client.net.command.SetCommand;
import com.alibaba.doris.client.net.command.StatCommand;
import com.alibaba.doris.client.net.command.CheckCommand.Type;
import com.alibaba.doris.client.net.command.result.CheckResult;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;

/**
 * LoggerFactory
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DorisClientHandler extends SimpleChannelUpstreamHandler implements ConnectionMetaOperation {

    private Logger log = LoggerFactory.getLogger(DorisClientHandler.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        Throwable t = e.getCause();
        log.error(ctx.toString(), t);
        if (t instanceof ConnectException) {
            return;
        }
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Command<?> cmd = (Command<?>) e.getMessage();
        if (null != cmd) {
            cmd.complete();
        }
    }

    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        this.channel = e.getChannel();
        super.channelOpen(ctx, e);
        if (log.isDebugEnabled()) {
            log.debug("Connection established! " + channel.getRemoteAddress());
        }
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Connection closed! " + channel.getLocalAddress());
        }
        super.channelClosed(ctx, e);
    }

    public OperationFuture<Boolean> delete(Key key) {
    	synchronized (lock) {
	        DeleteCommand commandData = new DeleteCommand(key);
	        channel.write(commandData);
	        return commandData.getResultFuture();
    	}
    }

    public OperationFuture<Value> get(Key key) {
    	synchronized (lock) {
	        GetCommand commandData = new GetCommand(key);
	        channel.write(commandData);
	        return commandData.getResultFuture();
    	}
    }

    public OperationFuture<Map<Key, Value>> gets(Set<Key> keys) {
        return null;
    }

    public OperationFuture<Boolean> put(Key key, Value value) {
    	synchronized (lock) {
	        Command<Boolean> commandData = new SetCommand(key, value);
	        channel.write(commandData);
	        return commandData.getResultFuture();
    	}
    }

    public OperationFuture<Boolean> puts(Map<Key, Value> map) {
        throw new UnsupportedOperationException();
    }

    public OperationFuture<String> migrate(String subcommand, String migrateRoute) {
    	synchronized (lock) {
	        Command<String> command = new MigrateCommand(subcommand, migrateRoute);
	        channel.write(command);
	        return command.getResultFuture();
    	}
    }

    public OperationFuture<String> stats(String viewType, int namespace) {
    	synchronized (lock) {
	        Command<String> commandData = new StatCommand(viewType, namespace);
	        channel.write(commandData);
	        return commandData.getResultFuture();
    	}
    }

    public OperationFuture<CheckResult> check(Type checkType) {
    	synchronized (lock) {
	        Command<CheckResult> commandData = new CheckCommand(checkType);
	        channel.write(commandData);
	        return commandData.getResultFuture();
    	}
    }

    public OperationFuture<Boolean> cad(Key key, Value value) {
    	synchronized (lock) {
	        CompareAndDeleteCommand commandData = new CompareAndDeleteCommand(key, value.getTimestamp());
	        channel.write(commandData);
	        return commandData.getResultFuture();
    	}
    }

    public OperationFuture<Boolean> cas(Key key, Value value) {
    	synchronized (lock) {
    		CompareAndSetCommand commandData = new CompareAndSetCommand(key, value);
    		channel.write(commandData);
    		return commandData.getResultFuture();
		}
    }

    private Channel channel;
    private Object lock = new Object();
}

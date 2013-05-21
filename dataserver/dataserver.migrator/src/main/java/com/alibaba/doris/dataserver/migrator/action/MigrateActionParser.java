package com.alibaba.doris.dataserver.migrator.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.common.data.util.ByteUtils;
import com.alibaba.doris.dataserver.action.data.ActionData;
import com.alibaba.doris.dataserver.action.parser.BaseActionParser;
import com.alibaba.doris.dataserver.migrator.MigrateActionType;
import com.alibaba.doris.dataserver.migrator.MigrationManager;
import com.alibaba.doris.dataserver.net.ByteBufferWrapper;
import com.alibaba.doris.dataserver.net.InvalidCommandException;
import com.alibaba.doris.dataserver.net.protocol.text.ProtocolConstant;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class MigrateActionParser extends BaseActionParser {

	private static final String NO_MESSAGE = "OK NO_MESSAGE";
    private static final Logger logger = LoggerFactory.getLogger(MigrationManager.class);

    /**
     * 返回结果
     * 
     * @param buffer
     * @param data
     */
    public void writeHead(ByteBufferWrapper buffer, ActionData data) {
        // write result into buffer.
        MigrationActionData actionData = (MigrationActionData) data;

        String message = actionData.getReturnMessage();

        if (message == null) {
        	if(logger.isDebugEnabled())
        		logger.debug("MigrateActionParser: return null message, action type: " + actionData.getActionType());
            message = NO_MESSAGE;
        }

        if (logger.isDebugEnabled()) 
        	logger.debug("Write migrate packet: " + message);

        byte[] writeBytes = ByteUtils.stringToByte(message);

        buffer.writeBytes(writeBytes);
        buffer.writeBytes(ProtocolConstant.CRLF);
    }

    /**
     * 解析指令body
     */
    public void readBody(byte[] body, ActionData data) {
        // read command string.
        String bodyString = ByteUtils.byteToString(body);

        MigrationActionData actionData = (MigrationActionData) data;
        actionData.setMigrationRouteString(bodyString);
    }

    /**
     * 解析指令头
     */
    public ActionData readHead(byte[] header, int startPos) {
        String commandString;

        String commmandName = MigrateActionType.MIGRATE.getName();
        commandString = ByteUtils.byteToString(header);

        if (commandString.charAt(commandString.length() - 2) == ProtocolConstant.CRLF[0]
            && commandString.charAt(commandString.length() - 1) == ProtocolConstant.CRLF[1]) {

            commandString = commandString.substring(0, commandString.length() - 2);
        }

        if (logger.isDebugEnabled()) logger.debug("Receive migrate command:" + commandString);

        try {
            MigrationActionData actionData = new MigrationActionData();
            String[] commandArgs = commandString.split(" ");

            if (commandArgs.length < 5) {
                throw new IllegalArgumentException("Argument insufficient of command:'" + commmandName
                                                   + "'. Require 5,but got " + commandArgs.length);
            }

            Short flag = Short.valueOf(commandArgs[2]);
            Long timestamp = Long.valueOf(commandArgs[3]);
            Integer bodyLen = Integer.valueOf(commandArgs[4]);

            actionData.setSubcommand(commandArgs[1]);
            actionData.setFlag(flag);
            actionData.setTimestamp(timestamp);
            actionData.setBodyBytes(bodyLen);

            if (bodyLen > 0) actionData.setNeedReadBody(true);
            return actionData;

        } catch (Exception e) {
            String msg = "Invalid 'migrate' command \"" + commandString + "\". Cause " + e.getMessage();
            logger.error(msg, e);
            throw new InvalidCommandException(msg);
        }
    }
}

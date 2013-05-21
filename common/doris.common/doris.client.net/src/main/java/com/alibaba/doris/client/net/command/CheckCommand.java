package com.alibaba.doris.client.net.command;

import com.alibaba.doris.client.net.command.result.CheckResult;
import com.alibaba.doris.client.net.protocol.ProtocolParser;
import com.alibaba.doris.client.net.protocol.text.CheckProtocolParser;
import com.alibaba.doris.common.data.util.ByteUtils;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class CheckCommand extends BaseCommand<CheckResult> {

    public CheckCommand(Type checkType) {
        this.checkType = checkType;
    }

    public Type getType() {
        return checkType;
    }

    public ProtocolParser getProtocolParser() {
        return parser;
    }

    public CheckResult getResult() {
        return new CheckResult() {

            public String getMessage() {
                return CheckCommand.this.getErrorMessage();
            }

            public boolean isSuccess() {
                return CheckCommand.this.isSuccess();
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(64);

        sb.append("CHECK");
        sb.append("{");
        sb.append("[status=");
        sb.append(this.isSuccess());
        sb.append("]");

        String message = this.getErrorMessage();
        if (null != message) {
            sb.append("[message=");
            sb.append(message);
            sb.append("]");
        }

        sb.append("}");
        return sb.toString();
    }

    private Type                        checkType;
    private static final ProtocolParser parser = new CheckProtocolParser();

    /**
     * 标识当前要Check的对象类型 目前有两种类型：CHECK_TEMP_NODE；CHECK_NORMAL_NODE
     * 
     * @see CheckType
     * @author ajun
     */
    public interface Type {

        /**
         * 获取check对象类型数据，字符串格式返回；
         * 
         * @return
         */
        byte[] getType();
    }

    /**
     * 检查临时节点: CHECK_STANDBY_NODE("check_standby_node")<br>
     * 检查临时节点: CHECK_TEMP_NODE("check_temp_node")<br>
     * 检查正常节点: CHECK_NORMAL_NODE("check_normal_node");
     * 
     * @author ajun
     */
    public enum CheckType implements Type {
        /**
         * 检查临时节点
         */
        CHECK_STANDBY_NODE("check_standby_node"),

        /**
         * 检查临时节点
         */
        CHECK_TEMP_NODE("check_temp_node"),

        /**
         * 检查正常节点；
         */
        CHECK_NORMAL_NODE("check_normal_node");

        private CheckType(String type) {
            this.type = ByteUtils.stringToByte(type);
        }

        public byte[] getType() {
            return type;
        }

        private byte[] type;
    }
}

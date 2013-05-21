package com.alibaba.doris.dataserver.net.protocol.text;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public final class ProtocolConstant {

    public static final byte[] CRLF                = { '\r', '\n' };
    public static final byte[] SPLID               = { ' ' };
    public static final byte   SPACE               = ' ';
    public static final byte[] NOREPLY             = { 'n', 'o', 'r', 'e', 'p', 'l', 'y' };

    public static final byte[] END                 = { 'E', 'N', 'D' };

    public static final byte[] STORED              = { 'S', 'T', 'O', 'R', 'E', 'D' };
    public static final byte[] NOT_STORED          = { 'N', 'O', 'T', '_', 'S', 'T', 'O', 'R', 'E', 'D' };
    public static final byte[] NOT_FOUND           = { 'N', 'O', 'T', '_', 'F', 'O', 'U', 'N', 'D' };
    public static final byte[] ERROR               = { 'E', 'R', 'R', 'O', 'R' };
    public static final byte[] VERSION_OUT_OF_DATE = { 'V', 'E', 'R', 'S', 'I', 'O', 'N', '_', 'O', 'U', 'T', '_', 'O',
            'F', '_', 'D', 'A', 'T', 'E'          };
    public static final byte[] CLIENT_ERROR        = { 'C', 'L', 'I', 'E', 'N', 'T', '_', 'E', 'R', 'R', 'O', 'R' };
    public static final byte[] SERVER_ERROR        = { 'S', 'E', 'R', 'V', 'E', 'R', '_', 'E', 'R', 'R', 'O', 'R' };
    public static final byte[] DELETED             = { 'D', 'E', 'L', 'E', 'T', 'E', 'D' };
    public static final byte[] DELETE_FAILED       = { 'D', 'E', 'L', 'E', 'T', 'E', '_', 'F', 'A', 'I', 'L', 'E', 'D' };
    public static final byte[] VALUE               = { 'V', 'A', 'L', 'U', 'E' };

    public static final byte[] NO_ERROR_MESSAGE    = { 'N', 'O', ' ', 'E', 'R', 'R', 'O', 'R', ' ', 'M', 'E', 'S', 'S',
            'A', 'G', 'E'                         };

    private ProtocolConstant() {

    }
}

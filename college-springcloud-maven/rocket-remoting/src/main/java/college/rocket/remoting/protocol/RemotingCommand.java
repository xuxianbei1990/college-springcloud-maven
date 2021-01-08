package college.rocket.remoting.protocol;

import college.rocket.remoting.CommandCustomHeader;
import lombok.Data;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: xuxianbei
 * Date: 2020/12/31
 * Time: 16:49
 * Version:V1.0
 */
@Data
public class RemotingCommand {

    private transient byte[] body;
    private static volatile int configVersion = -1;
    private transient CommandCustomHeader customHeader;
    private static final int RPC_TYPE = 0;
    private int code;
    private int version = 0;
    private int flag = 0;
    public static final String REMOTING_VERSION_KEY = "rocketmq.remoting.version";
    private static AtomicInteger requestId = new AtomicInteger(0);
    private String remark;
    private static SerializeType serializeTypeConfigInThisServer = SerializeType.JSON;

    private int opaque = requestId.getAndIncrement();

    public ByteBuffer encodeHeader() {
        return encodeHeader(this.body != null ? this.body.length : 0);
    }

    public ByteBuffer encodeHeader(final int bodyLength) {

        return null;
    }

    public static RemotingCommand decode(final ByteBuffer byteBuffer) {
        return null;
    }

    public static RemotingCommand createRequestCommand(int code, CommandCustomHeader customHeader) {
        RemotingCommand cmd = new RemotingCommand();
        cmd.setCode(code);
        cmd.customHeader = customHeader;
        setCmdVersion(cmd);
        return cmd;
    }

    private static void setCmdVersion(RemotingCommand cmd) {
        if (configVersion >= 0) {
            cmd.setVersion(configVersion);
        } else {
            String v = System.getProperty(REMOTING_VERSION_KEY);
            if (v != null) {
                int value = Integer.parseInt(v);
                cmd.setVersion(value);
                configVersion = value;
            }
        }
    }

    public byte[] getBody() {
        return body;
    }

    public boolean isResponseType() {
        int bits = 1 << RPC_TYPE;
        return (this.flag & bits) == bits;
    }

    public RemotingCommandType getType() {
        if (this.isResponseType()) {
            return RemotingCommandType.RESPONSE_COMMAND;
        }

        return RemotingCommandType.REQUEST_COMMAND;
    }

    public static SerializeType getSerializeTypeConfigInThisServer() {
        return serializeTypeConfigInThisServer;
    }
}

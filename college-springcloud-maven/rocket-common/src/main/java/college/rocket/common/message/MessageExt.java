package college.rocket.common.message;

import lombok.Data;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

/**
 * @author: xuxianbei
 * Date: 2020/12/30
 * Time: 18:23
 * Version:V1.0
 */
@Data
public class MessageExt extends Message {


    private int sysFlag = 0;

    private int queueId;

    private SocketAddress bornHost;

    private SocketAddress storeHost;

    private long bornTimestamp;
    private long storeTimestamp;

    private int reconsumeTimes;
    private int bodyCRC;


    public ByteBuffer getStoreHostBytes() {
        return socketAddress2ByteBuffer(this.storeHost);
    }

    public ByteBuffer getStoreHostBytes(ByteBuffer byteBuffer) {
        return socketAddress2ByteBuffer(this.storeHost, byteBuffer);
    }

    public static ByteBuffer socketAddress2ByteBuffer(SocketAddress socketAddress) {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
        InetAddress address = inetSocketAddress.getAddress();
        ByteBuffer byteBuffer;
        if (address instanceof Inet4Address) {
            byteBuffer = ByteBuffer.allocate(4 + 4);
        } else {
            byteBuffer = ByteBuffer.allocate(16 + 4);
        }
        return socketAddress2ByteBuffer(socketAddress, byteBuffer);
    }

    public static ByteBuffer socketAddress2ByteBuffer(final SocketAddress socketAddress, final ByteBuffer byteBuffer) {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
        InetAddress address = inetSocketAddress.getAddress();
        if (address instanceof Inet4Address) {
            byteBuffer.put(inetSocketAddress.getAddress().getAddress(), 0, 4);
        } else {
            byteBuffer.put(inetSocketAddress.getAddress().getAddress(), 0, 16);
        }
        byteBuffer.putInt(inetSocketAddress.getPort());
        byteBuffer.flip();
        return byteBuffer;
    }

    public ByteBuffer getBornHostBytes(ByteBuffer byteBuffer) {
        return socketAddress2ByteBuffer(this.bornHost, byteBuffer);
    }
}

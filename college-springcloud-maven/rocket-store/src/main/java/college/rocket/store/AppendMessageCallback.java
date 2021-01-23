package college.rocket.store;

import java.nio.ByteBuffer;

/**
 * @author: xuxianbei
 * Date: 2021/1/23
 * Time: 11:37
 * Version:V1.0
 */
public interface AppendMessageCallback {

    /**
     * After message serialization, write MapedByteBuffer
     *
     * @return How many bytes to write
     */
    AppendMessageResult doAppend(final long fileFromOffset, final ByteBuffer byteBuffer,
                                 final int maxBlank, final MessageExtBrokerInner msg);
}

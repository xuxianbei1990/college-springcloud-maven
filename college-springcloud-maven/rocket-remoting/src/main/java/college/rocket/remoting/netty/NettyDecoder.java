package college.rocket.remoting.netty;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 消息解码
 * @author: xuxianbei
 * Date: 2020/12/31
 * Time: 17:52
 * Version:V1.0
 * LengthFieldBasedFrameDecoder： 处理粘包，拆包情况
 */
public class NettyDecoder extends LengthFieldBasedFrameDecoder {

    private static final int FRAME_MAX_LENGTH = 16777216;

    public NettyDecoder() {
        super(FRAME_MAX_LENGTH, 0, 4, 0, 4);
    }
}

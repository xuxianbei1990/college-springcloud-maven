package college.rocket.remoting.netty;

import college.rocket.remoting.protocol.RemotingCommand;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 消息编码
 * 消息转换成字节数组
 * @author: xuxianbei
 * Date: 2020/12/31
 * Time: 17:35
 * Version:V1.0
 */
@ChannelHandler.Sharable
public class NettyEncoder extends MessageToByteEncoder<RemotingCommand> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RemotingCommand remotingCommand, ByteBuf byteBuf) throws Exception {

    }
}

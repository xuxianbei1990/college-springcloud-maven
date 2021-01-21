package college.rocket.broker.processor;

import college.rocket.broker.BrokerController;
import college.rocket.common.protocol.RequestCode;
import college.rocket.common.protocol.header.SendMessageRequestHeader;
import college.rocket.common.protocol.header.SendMessageRequestHeaderV2;
import college.rocket.remoting.exception.RemotingCommandException;
import college.rocket.remoting.netty.AsyncNettyRequestProcessor;
import college.rocket.remoting.netty.NettyRequestProcessor;
import college.rocket.remoting.protocol.RemotingCommand;
import lombok.Data;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author: xuxianbei
 * Date: 2021/1/21
 * Time: 10:12
 * Version:V1.0
 */
@Data
public abstract class AbstractSendMessageProcessor extends AsyncNettyRequestProcessor implements NettyRequestProcessor {

    protected final BrokerController brokerController;

    protected final SocketAddress storeHost;

    public AbstractSendMessageProcessor(final BrokerController brokerController) {
        this.brokerController = brokerController;
        this.storeHost =
                new InetSocketAddress(brokerController.getBrokerConfig().getBrokerIP1(), brokerController
                        .getNettyServerConfig().getListenPort());
    }

    protected SendMessageRequestHeader parseRequestHeader(RemotingCommand request)
            throws RemotingCommandException {
        SendMessageRequestHeaderV2 requestHeaderV2 = null;
        SendMessageRequestHeader requestHeader = null;
        switch (request.getCode()) {
            case RequestCode.SEND_MESSAGE_V2:
                requestHeaderV2 =
                        (SendMessageRequestHeaderV2) request
                                .decodeCommandCustomHeader(SendMessageRequestHeaderV2.class);
            case RequestCode.SEND_MESSAGE:
                if (null == requestHeaderV2) {
                    requestHeader =
                            (SendMessageRequestHeader) request
                                    .decodeCommandCustomHeader(SendMessageRequestHeader.class);
                }
            default:
                break;
        }
        return requestHeader;
    }
}

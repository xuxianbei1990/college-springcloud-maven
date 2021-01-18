package college.rocket.namesrv.processor;

import college.rocket.common.UtilAll;
import college.rocket.common.namesrv.RegisterBrokerResult;
import college.rocket.common.protocol.RequestCode;
import college.rocket.common.protocol.ResponseCode;
import college.rocket.common.protocol.body.RegisterBrokerBody;
import college.rocket.common.protocol.header.namesrv.GetRouteInfoRequestHeader;
import college.rocket.common.protocol.namesrv.RegisterBrokerRequestHeader;
import college.rocket.common.protocol.namesrv.RegisterBrokerResponseHeader;
import college.rocket.common.protocol.route.TopicRouteData;
import college.rocket.namesrv.NamesrvController;
import college.rocket.remoting.common.RemotingHelper;
import college.rocket.remoting.exception.RemotingCommandException;
import college.rocket.remoting.netty.AsyncNettyRequestProcessor;
import college.rocket.remoting.netty.NettyRequestProcessor;
import college.rocket.remoting.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: xuxianbei
 * Date: 2021/1/8
 * Time: 17:13
 * Version:V1.0
 */
@Slf4j
public class DefaultRequestProcessor extends AsyncNettyRequestProcessor implements NettyRequestProcessor {

    protected final NamesrvController namesrvController;

    public DefaultRequestProcessor(NamesrvController namesrvController) {
        this.namesrvController = namesrvController;
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws RemotingCommandException {

        if (ctx != null) {
            log.info("receive request, {} {} {}",
                    request.getCode(), RemotingHelper.parseChannelRemoteAddr(ctx.channel()), request);
        }
        switch (request.getCode()) {
            case RequestCode.GET_ROUTEINFO_BY_TOPIC:
                return this.getRouteInfoByTopic(ctx, request);
            case RequestCode.REGISTER_BROKER:
                return this.registerBrokerWithFilterServer(ctx, request);
        }
        return null;
    }

    private RemotingCommand registerBrokerWithFilterServer(ChannelHandlerContext ctx, RemotingCommand request) throws RemotingCommandException {
        final RemotingCommand response = RemotingCommand.createResponseCommand(RegisterBrokerResponseHeader.class);
        final RegisterBrokerResponseHeader responseHeader = (RegisterBrokerResponseHeader) response.getCustomHeader();
        final RegisterBrokerRequestHeader requestHeader =
                (RegisterBrokerRequestHeader) request.decodeCommandCustomHeader(RegisterBrokerRequestHeader.class);
        //校验通讯
        if (!checksum(ctx, request, requestHeader)) {
            response.setCode(ResponseCode.SYSTEM_ERROR);
            response.setRemark("crc32 not match");
            return response;
        }

        RegisterBrokerBody registerBrokerBody = new RegisterBrokerBody();
        //解析 注册请求体
        if (request.getBody() != null) {
            try {
                registerBrokerBody = RegisterBrokerBody.decode(request.getBody(), requestHeader.isCompressed());
            } catch (Exception e) {
                throw new RemotingCommandException("Failed to decode RegisterBrokerBody", e);
            }
        } else {
            registerBrokerBody.getTopicConfigSerializeWrapper().getDataVersion().setCounter(new AtomicLong(0));
            registerBrokerBody.getTopicConfigSerializeWrapper().getDataVersion().setTimestamp(0);
        }
        RegisterBrokerResult result = this.namesrvController.getRouteInfoManager().registerBroker(requestHeader.getClusterName(),
                requestHeader.getBrokerAddr(),
                requestHeader.getBrokerName(),
                requestHeader.getBrokerId(),
                "requestHeader.getHaServerAddr()",
                registerBrokerBody.getTopicConfigSerializeWrapper(),
                registerBrokerBody.getFilterServerList(),
                ctx.channel());

//        responseHeader.setHaServerAddr(result.getHaServerAddr());
        responseHeader.setMasterAddr(result.getMasterAddr());
        response.setCode(ResponseCode.SUCCESS);
        response.setRemark(null);
        return response;
    }

    private boolean checksum(ChannelHandlerContext ctx, RemotingCommand request, RegisterBrokerRequestHeader requestHeader) {
        if (requestHeader.getBodyCrc32() != 0) {
            final int crc32 = UtilAll.crc32(request.getBody());
            if (crc32 != requestHeader.getBodyCrc32()) {
                log.warn(String.format("receive registerBroker request,crc32 not match,from %s",
                        RemotingHelper.parseChannelRemoteAddr(ctx.channel())));
                return false;
            }
        }
        return true;
    }

    private RemotingCommand getRouteInfoByTopic(ChannelHandlerContext ctx, RemotingCommand request) throws RemotingCommandException {
        final RemotingCommand response = RemotingCommand.createResponseCommand(null);
        final GetRouteInfoRequestHeader requestHeader =
                (GetRouteInfoRequestHeader) request.decodeCommandCustomHeader(GetRouteInfoRequestHeader.class);
        TopicRouteData topicRouteData = this.namesrvController.getRouteInfoManager().pickupTopicRouteData(requestHeader.getTopic());
        if (topicRouteData != null) {

        }
        response.setCode(ResponseCode.TOPIC_NOT_EXIST);
        response.setRemark("No topic route info in name server for the topic: " + "requestHeader.getTopic()");
        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}

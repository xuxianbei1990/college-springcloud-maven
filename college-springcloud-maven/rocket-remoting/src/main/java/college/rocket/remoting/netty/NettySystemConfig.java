package college.rocket.remoting.netty;

/**
 * @author: xuxianbei
 * Date: 2020/12/31
 * Time: 17:17
 * Version:V1.0
 */
public class NettySystemConfig {

    public static int socketSndbufSize = 65535;
    public static int socketRcvbufSize = 65535;

    public static final int CLIENT_ONEWAY_SEMAPHORE_VALUE = 65535;
    public static final int CLIENT_ASYNC_SEMAPHORE_VALUE = 65535;
}

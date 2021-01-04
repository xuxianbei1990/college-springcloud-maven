package college.rocketmq.client.impl;

import college.rocket.remoting.RPCHook;
import college.rocketmq.client.ClientConfig;
import college.rocketmq.client.impl.factory.MQClientInstance;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: xuxianbei
 * Date: 2020/12/30
 * Time: 17:33
 * Version:V1.0
 */
public class MQClientManager {
    private static MQClientManager instance = new MQClientManager();

    private AtomicInteger factoryIndexGenerator = new AtomicInteger();

    private ConcurrentMap<String/* clientId */, MQClientInstance> factoryTable =
            new ConcurrentHashMap<String, MQClientInstance>();

    public static MQClientManager getInstance() {
        return instance;
    }

    public MQClientInstance getOrCreateMQClientInstance(final ClientConfig clientConfig, RPCHook rpcHook) {
        String college = "college";
        MQClientInstance instance = factoryTable.get(college);
        if (null == instance) {
            instance = new MQClientInstance(clientConfig.cloneClientConfig(),
                    this.factoryIndexGenerator.getAndIncrement(), college, rpcHook);
        }
        return instance;
    }
}

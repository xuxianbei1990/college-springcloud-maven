package college.rocket.broker.filtersrv;

import college.rocket.broker.BrokerController;
import college.rocket.common.ThreadFactoryImpl;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;

/**
 * 这个不知道干什么用的
 *
 * @author: xuxianbei
 * Date: 2021/1/14
 * Time: 11:30
 * Version:V1.0
 */
@Slf4j
public class FilterServerManager {

    private final BrokerController brokerController;

    private final ConcurrentMap<Channel, FilterServerInfo> filterServerTable =
            new ConcurrentHashMap<Channel, FilterServerInfo>(16);

    private ScheduledExecutorService scheduledExecutorService = Executors
            .newSingleThreadScheduledExecutor(new ThreadFactoryImpl("FilterServerManagerScheduledThread"));

    public FilterServerManager(final BrokerController brokerController) {
        this.brokerController = brokerController;
    }

    public List<String> buildNewFilterServerList() {
        return null;
    }

    public void start() {

        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    FilterServerManager.this.createFilterServer();
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        }, 1000 * 5, 1000 * 30, TimeUnit.MILLISECONDS);
    }

    public void createFilterServer() {
        int more =
                this.brokerController.getBrokerConfig().getFilterServerNums() - this.filterServerTable.size();
        String cmd = this.buildStartCommand();
        for (int i = 0; i < more; i++) {
//            FilterServerUtil.callShell(cmd, log);
        }
    }

    public void scanNotActiveChannel() {

    }


    private String buildStartCommand() {
        return "";
    }

    static class FilterServerInfo {

    }
}

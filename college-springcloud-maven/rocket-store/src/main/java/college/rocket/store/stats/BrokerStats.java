package college.rocket.store.stats;

import college.rocket.store.DefaultMessageStore;
import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2021/3/6
 * Time: 16:28
 * Version:V1.0
 */
@Data
public class BrokerStats {

    private final DefaultMessageStore defaultMessageStore;

    public BrokerStats(DefaultMessageStore defaultMessageStore) {
        this.defaultMessageStore = defaultMessageStore;
    }
}

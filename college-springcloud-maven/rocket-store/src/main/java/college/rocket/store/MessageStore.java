package college.rocket.store;

import java.util.concurrent.CompletableFuture;

/**
 * @author: xuxianbei
 * Date: 2021/1/21
 * Time: 16:32
 * Version:V1.0
 */
public interface MessageStore {

    default CompletableFuture<PutMessageResult> asyncPutMessage(final MessageExtBrokerInner msg) {
        return CompletableFuture.completedFuture(putMessage(msg));
    }

    PutMessageResult putMessage(final MessageExtBrokerInner msg);

    long getMaxOffsetInQueue(final String topic, final int queueId);

    /**
     * Update HA master address.
     *
     * @param newAddr new address.
     */
    void updateHaMasterAddress(final String newAddr);

    void start() throws Exception;
}

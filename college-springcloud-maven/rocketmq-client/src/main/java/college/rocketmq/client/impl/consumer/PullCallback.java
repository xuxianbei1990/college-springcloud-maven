package college.rocketmq.client.impl.consumer;

import college.rocketmq.client.consumer.PullResult;

/**
 * @author: xuxianbei
 * Date: 2021/2/1
 * Time: 10:32
 * Version:V1.0
 */
public interface PullCallback {

    void onSuccess(final PullResult pullResult);

    void onException(final Throwable e);
}

package college.rocketmq.client.impl;

import lombok.Data;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: xuxianbei
 * Date: 2021/1/27
 * Time: 17:42
 * Version:V1.0
 */
@Data
public class ProcessQueue {
    private final AtomicLong msgCount = new AtomicLong();
    private volatile boolean dropped = false;
    private volatile long lastPullTimestamp = System.currentTimeMillis();
}

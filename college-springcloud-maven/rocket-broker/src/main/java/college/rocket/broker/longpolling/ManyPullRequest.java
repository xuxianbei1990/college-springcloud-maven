package college.rocket.broker.longpolling;

import java.util.ArrayList;

/**
 * @author: xuxianbei
 * Date: 2021/2/1
 * Time: 14:35
 * Version:V1.0
 */
public class ManyPullRequest {
    private final ArrayList<PullRequest> pullRequestList = new ArrayList<>();

    public synchronized void addPullRequest(final PullRequest pullRequest) {
        this.pullRequestList.add(pullRequest);
    }
}

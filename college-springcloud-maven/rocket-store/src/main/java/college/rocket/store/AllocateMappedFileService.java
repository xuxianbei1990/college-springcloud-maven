package college.rocket.store;

import college.rocket.remoting.common.ServiceThread;

/**
 * @author: xuxianbei
 * Date: 2021/1/23
 * Time: 11:15
 * Version:V1.0
 */
public class AllocateMappedFileService extends ServiceThread {

    private DefaultMessageStore messageStore;

    public AllocateMappedFileService(DefaultMessageStore messageStore) {
        this.messageStore = messageStore;
    }

    @Override
    public String getServiceName() {
        return null;
    }

    @Override
    public void run() {

    }
}

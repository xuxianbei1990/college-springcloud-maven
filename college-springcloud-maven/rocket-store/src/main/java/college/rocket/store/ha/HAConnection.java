package college.rocket.store.ha;

import college.rocket.remoting.common.RemotingUtil;
import college.rocket.remoting.common.ServiceThread;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author: xuxianbei
 * Date: 2021/3/18
 * Time: 15:25
 * Version:V1.0
 */
@Slf4j
@Data
public class HAConnection {

    private final HAService haService;
    private final SocketChannel socketChannel;

    private volatile long slaveRequestOffset = -1;

    private WriteSocketService writeSocketService;
    private ReadSocketService readSocketService;

    public HAConnection(final HAService haService, final SocketChannel socketChannel) throws IOException {
        this.haService = haService;

        this.socketChannel = socketChannel;

        this.writeSocketService = new WriteSocketService(this.socketChannel);
        this.readSocketService = new ReadSocketService(this.socketChannel);
    }

    public void start() {
        this.readSocketService.start();
        this.writeSocketService.start();
    }

    class ReadSocketService extends ServiceThread {

        public ReadSocketService(final SocketChannel socketChannel) throws IOException {

        }

        @Override
        public String getServiceName() {
            return null;
        }

        @Override
        public void run() {

        }
    }

    class WriteSocketService extends ServiceThread {
        private final Selector selector;
        private final SocketChannel socketChannel;
        private long nextTransferFromWhere = -1;

        public WriteSocketService(final SocketChannel socketChannel) throws IOException {
            this.selector = RemotingUtil.openSelector();
            this.socketChannel = socketChannel;
            this.socketChannel.register(this.selector, SelectionKey.OP_WRITE);
            this.setDaemon(true);
        }

        @Override
        public String getServiceName() {
            return null;
        }

        @Override
        public void run() {
            HAConnection.log.info(this.getServiceName() + " service started");
            while (!this.isStopped()) {
                try {
                    this.selector.select(1000);
                    if (-1 == HAConnection.this.slaveRequestOffset) {
                        Thread.sleep(10);
                        continue;
                    }

                    if (-1 == this.nextTransferFromWhere) {
                        if (0 == HAConnection.this.slaveRequestOffset) {

                        }
                    }

                } catch (Exception e) {

                    HAConnection.log.error(this.getServiceName() + " service has exception.", e);
                    break;
                }
            }

        }
    }
}

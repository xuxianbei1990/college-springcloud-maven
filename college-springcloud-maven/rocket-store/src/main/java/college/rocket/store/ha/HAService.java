package college.rocket.store.ha;

import college.rocket.remoting.common.RemotingUtil;
import college.rocket.remoting.common.ServiceThread;
import college.rocket.store.DefaultMessageStore;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author: xuxianbei
 * Date: 2021/3/16
 * Time: 14:29
 * Version:V1.0
 */
@Slf4j
public class HAService {

    private final DefaultMessageStore defaultMessageStore;

    private final HAClient haClient;

    public HAService(final DefaultMessageStore defaultMessageStore) throws IOException {
        this.defaultMessageStore = defaultMessageStore;

        this.haClient = new HAClient();
    }

    public void updateMasterAddress(final String newAddr) {
        if (this.haClient != null) {
            this.haClient.updateMasterAddress(newAddr);
        }
    }

    public void start() throws Exception {
        this.haClient.start();
    }


    class HAClient extends ServiceThread {

        private static final int READ_MAX_BUFFER_SIZE = 1024 * 1024 * 4;

        private Selector selector;

        private ByteBuffer byteBufferRead = ByteBuffer.allocate(READ_MAX_BUFFER_SIZE);

        private long currentReportedOffset = 0;

        private long lastWriteTimestamp = System.currentTimeMillis();

        public HAClient() throws IOException {
            this.selector = RemotingUtil.openSelector();
        }

        private boolean connectMaster() throws ClosedChannelException {
            if (null == socketChannel) {
                String addr = this.masterAddress.get();
                if (addr != null) {
                    SocketAddress socketAddress = RemotingUtil.string2SocketAddress(addr);
                    if (socketAddress != null) {
                        //进行NIO 发起连接
                        this.socketChannel = RemotingUtil.connect(socketAddress);
                        if (this.socketChannel != null) {
                            this.socketChannel.register(this.selector, SelectionKey.OP_READ);
                        }
                    }
                }
                this.currentReportedOffset = HAService.this.defaultMessageStore.getMaxPhyOffset();
                this.lastWriteTimestamp = System.currentTimeMillis();
            }
            return this.socketChannel != null;
        }

        private SocketChannel socketChannel;

        private final AtomicReference<String> masterAddress = new AtomicReference<>();


        @Override
        public String getServiceName() {
            return null;
        }

        @Override
        public void run() {
            log.info(this.getServiceName() + " service started");

            while (!this.isStopped()) {
                try {
                    //通过NIO和Master建立连接
                    if (this.connectMaster()) {
                        //貌似处理数据同步
                        if (this.isTimeToReportOffset()) {

                        }
                        this.selector.select(1000);
                        boolean ok = this.processReadEvent();

                    }
                } catch (Exception e) {
                    log.error(this.getServiceName() + " service has exception.", e);
                }
            }


        }

        private boolean processReadEvent() {
            int readSizeZeroTimes = 0;
            while (this.byteBufferRead.hasRemaining()) {

            }
            return false;
        }

        private boolean isTimeToReportOffset() {
            long interval =
                    System.currentTimeMillis() - this.lastWriteTimestamp;
            boolean needHeart = interval > HAService.this.defaultMessageStore.getMessageStoreConfig().getHaSendHeartbeatInterval();

            return needHeart;
        }

        public void updateMasterAddress(final String newAddr) {
            String currentAddr = this.masterAddress.get();
            if (currentAddr == null || !currentAddr.equals(newAddr)) {
                this.masterAddress.set(newAddr);
                log.info("update master address, OLD: " + currentAddr + " NEW: " + newAddr);
            }
        }
    }
}

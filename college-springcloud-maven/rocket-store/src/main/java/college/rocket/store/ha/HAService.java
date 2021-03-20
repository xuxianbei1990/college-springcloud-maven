package college.rocket.store.ha;

import college.rocket.remoting.common.RemotingUtil;
import college.rocket.remoting.common.ServiceThread;
import college.rocket.store.DefaultMessageStore;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author: xuxianbei
 * Date: 2021/3/16
 * Time: 14:29
 * Version:V1.0
 */
@Slf4j
public class HAService {

    private final List<HAConnection> connectionList = new LinkedList<>();

    private final DefaultMessageStore defaultMessageStore;

    private final AcceptSocketService acceptSocketService;

    private final GroupTransferService groupTransferService;

    private final HAClient haClient;

    public HAService(final DefaultMessageStore defaultMessageStore) throws IOException {
        this.defaultMessageStore = defaultMessageStore;

        //接收客户端发起的链接
        this.acceptSocketService =
                new AcceptSocketService(defaultMessageStore.getMessageStoreConfig().getHaListenPort());
        this.groupTransferService = new GroupTransferService();
        //启动客户端
        this.haClient = new HAClient();
    }

    public void updateMasterAddress(final String newAddr) {
        if (this.haClient != null) {
            this.haClient.updateMasterAddress(newAddr);
        }
    }

    public void start() throws Exception {
        this.acceptSocketService.beginAccept();
        this.acceptSocketService.start();
        this.groupTransferService.start();
        this.haClient.start();
    }


    class GroupTransferService extends ServiceThread {

        @Override
        public String getServiceName() {
            return null;
        }

        @Override
        public void run() {

        }
    }

    public void addConnection(final HAConnection conn) {
        synchronized (this.connectionList) {
            this.connectionList.add(conn);
        }
    }


    /**
     * 服务端nio代码
     */
    class AcceptSocketService extends ServiceThread {
        private final SocketAddress socketAddressListen;
        private ServerSocketChannel serverSocketChannel;
        private Selector selector;

        public AcceptSocketService(final int port) {
            this.socketAddressListen = new InetSocketAddress(port);
        }

        @Override
        public String getServiceName() {
            return null;
        }

        @Override
        public void run() {
            log.info(this.getServiceName() + " service started");

            while (!this.isStopped()) {
                try {
                    this.selector.select(1000);
                    Set<SelectionKey> selected = this.selector.selectedKeys();
                    if (selected != null) {
                        for (SelectionKey k : selected) {
                            if ((k.readyOps() & SelectionKey.OP_ACCEPT) != 0) {
                                SocketChannel sc = ((ServerSocketChannel) k.channel()).accept();
                                if (sc != null) {
                                    HAService.log.info("HAService receive new connection, "
                                            + sc.socket().getRemoteSocketAddress());

                                    try {
                                        HAConnection conn = new HAConnection(HAService.this, sc);
                                        conn.start();
                                        HAService.this.addConnection(conn);
                                    } catch (Exception e) {
                                        log.error("new HAConnection exception", e);
                                        sc.close();
                                    }
                                }
                            }

                        }
                    }

                } catch (Exception e) {
                    log.error(this.getServiceName() + " service has exception.", e);
                }
            }

            log.info(this.getServiceName() + " service end");
        }

        public void beginAccept() throws Exception {
            this.serverSocketChannel = ServerSocketChannel.open();
            this.selector = RemotingUtil.openSelector();
            this.serverSocketChannel.socket().setReuseAddress(true);
            this.serverSocketChannel.socket().bind(this.socketAddressListen);
            this.serverSocketChannel.configureBlocking(false);
            this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
        }
    }

    /**
     * Salve 会启动这个客户端，然后连接Master
     * 然后把数据拉取过来，写到自己CommitLog中去
     */
    class HAClient extends ServiceThread {

        //4M
        private static final int READ_MAX_BUFFER_SIZE = 1024 * 1024 * 4;

        private final ByteBuffer reportOffset = ByteBuffer.allocate(8);

        private Selector selector;

        private ByteBuffer byteBufferRead = ByteBuffer.allocate(READ_MAX_BUFFER_SIZE);
        private ByteBuffer byteBufferBackup = ByteBuffer.allocate(READ_MAX_BUFFER_SIZE);

        private long currentReportedOffset = 0;

        private long lastWriteTimestamp = System.currentTimeMillis();
        private int dispatchPosition = 0;

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
                        //处理数据同步
                        if (this.isTimeToReportOffset()) {
                            boolean result = this.reportSlaveMaxOffset(this.currentReportedOffset);
                            if (!result) {
                                this.closeMaster();
                                log.error("HAClient, reportSlaveMaxOffset error, " + this.currentReportedOffset);
                            }
                        }
                        this.selector.select(1000);

                        boolean ok = this.processReadEvent();
                        if (!ok) {
                            this.closeMaster();
                        }
                        if (!reportSlaveMaxOffsetPlus()) {
                            continue;
                        }
                        //确保数据一定更新
                        long interval = System.currentTimeMillis() - this.lastWriteTimestamp;
                        if (interval > HAService.this.getDefaultMessageStore().getMessageStoreConfig().getHaHousekeepingInterval()) {
                            log.warn("HAClient, housekeeping, found this connection[" + this.masterAddress
                                    + "] expired, " + interval);
                            this.closeMaster();
                            log.warn("HAClient, master not response some time, so close connection");
                        }

                    }
                } catch (Exception e) {
                    log.error(this.getServiceName() + " service has exception.", e);
                    this.waitForRunning(1000 * 5);
                }
            }

            log.info(this.getServiceName() + " service end");

        }

        private boolean reportSlaveMaxOffsetPlus() {
            boolean result = true;
            long currentPhyOffset = HAService.this.defaultMessageStore.getMaxPhyOffset();
            if (currentPhyOffset > this.currentReportedOffset) {
                this.currentReportedOffset = currentPhyOffset;
                //修正RocketMQ的游标
                result = this.reportSlaveMaxOffset(this.currentReportedOffset);
                if (!result) {
                    this.closeMaster();
                    log.error("HAClient, reportSlaveMaxOffset error, " + this.currentReportedOffset);
                }
            }
            return result;
        }

        private void closeMaster() {
            if (null != this.socketChannel) {
                try {

                    SelectionKey sk = this.socketChannel.keyFor(this.selector);
                    if (sk != null) {
                        sk.cancel();
                    }
                    this.socketChannel.close();
                    this.socketChannel = null;
                } catch (IOException e) {
                    log.warn("closeMaster exception. ", e);
                }

                this.lastWriteTimestamp = 0;
                this.dispatchPosition = 0;

                this.byteBufferBackup.position(0);
                this.byteBufferBackup.limit(READ_MAX_BUFFER_SIZE);

                this.byteBufferRead.position(0);
                this.byteBufferRead.limit(READ_MAX_BUFFER_SIZE);
            }
        }

        private boolean reportSlaveMaxOffset(long maxOffset) {
            this.reportOffset.position(0);
            this.reportOffset.limit(8);
            this.reportOffset.putLong(maxOffset);
            this.reportOffset.position(0);
            this.reportOffset.limit(8);
            for (int i = 0; i < 3 && this.reportOffset.hasRemaining(); i++) {
                try {
                    this.socketChannel.write(this.reportOffset);
                } catch (IOException e) {
                    log.error(this.getServiceName()
                            + "reportSlaveMaxOffset this.socketChannel.write exception", e);
                    return false;
                }
            }
            lastWriteTimestamp = System.currentTimeMillis();
            return !this.reportOffset.hasRemaining();
        }

        private boolean processReadEvent() {
            int readSizeZeroTimes = 0;
            while (this.byteBufferRead.hasRemaining()) {
                try {
                    //我有一个问题？  这里没有事件监听？ 确实没有
                    //从管道中把数据放到byteBufferRead中去
                    int readSize = this.socketChannel.read(this.byteBufferRead);
                    if (readSize > 0) {
                        readSizeZeroTimes = 0;
                        boolean result = this.dispatchReadRequest();
                        if (!result) {
                            log.error("HAClient, dispatchReadRequest error");
                            return false;
                        }
                    } else if (readSize == 0) {
                        if (++readSizeZeroTimes >= 3) {
                            break;
                        }
                    } else {
                        log.info("HAClient, processReadEvent read socket < 0");
                        return false;
                    }

                } catch (IOException e) {
                    log.info("HAClient, processReadEvent read socket exception", e);
                    return false;
                }
            }
            return true;
        }

        private boolean dispatchReadRequest() {
            final int msgHeaderSize = 8 + 4; // phyoffset + size
            int readSocketPos = this.byteBufferRead.position();

            while (true) {
                int diff = this.byteBufferRead.position() - this.dispatchPosition;
                if (diff >= msgHeaderSize) {
                    //这个是从Master读过来的
                    long masterPhyOffset = this.byteBufferRead.getLong(this.dispatchPosition);
                    int bodySize = this.byteBufferRead.getInt(this.dispatchPosition + 8);
                    long slavePhyOffset = HAService.this.defaultMessageStore.getMaxPhyOffset();

                    if (slavePhyOffset != 0) {
                        //这里我有一个疑问
                        if (slavePhyOffset != masterPhyOffset) {
                            log.error("master pushed offset not equal the max phy offset in slave, SLAVE: "
                                    + slavePhyOffset + " MASTER: " + masterPhyOffset);
                            return false;
                        }
                    }

                    if (diff >= (msgHeaderSize + bodySize)) {
                        //移位读取数据
                        byte[] bodyData = new byte[bodySize];
                        this.byteBufferRead.position(this.dispatchPosition + msgHeaderSize);
                        this.byteBufferRead.get(bodyData);
                        //把Master数据读取过来放到自己的commitLog
                        HAService.this.defaultMessageStore.appendToCommitLog(masterPhyOffset, bodyData);
                    }
                    break;
                }
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

    public DefaultMessageStore getDefaultMessageStore() {
        return defaultMessageStore;
    }

    public static void main(String[] args) throws IOException {
        ByteBuffer reportOffset = ByteBuffer.allocate(8);
        //读取第一个位置
        reportOffset.position(0);
        //读到第八位结束
        reportOffset.limit(8);
//        reportOffset.put("zhong".getBytes("UTF-8"));
        //Long是8个字节，从左往右放入，高位为0，低位为具体数值 一个字节最多表示255 一个字节8位。
        reportOffset.putLong(300L); //占用8个字节：0000 0001
        reportOffset.position(0);
        reportOffset.limit(8);
        File file = new File("/test.txt");

        FileOutputStream outputStream = new FileOutputStream(file);
        FileChannel fileChannelOutput = outputStream.getChannel();
        fileChannelOutput.write(reportOffset);
        //把数据刷到文件
        reportOffset.flip();
        fileChannelOutput.close();
        outputStream.close();


        ByteBuffer reportOffset2 = ByteBuffer.allocate(8);
        reportOffset.position(0);
        reportOffset.limit(8);

        FileInputStream inputStream = new FileInputStream(file);
        FileChannel fileChannel = inputStream.getChannel();

        fileChannel.read(reportOffset2);
        //把数据刷到内存
        reportOffset2.flip();
        reportOffset.position(0);
        reportOffset.limit(8);
        byte[] bytes = new byte[8]; //读取是从左往右读取4位
        reportOffset.get(bytes);
        System.out.println(new String(bytes));
        fileChannel.close();
        inputStream.close();
        System.out.println(reportOffset2.getLong());

    }
}

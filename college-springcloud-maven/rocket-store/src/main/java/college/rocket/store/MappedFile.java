package college.rocket.store;

import college.rocket.common.message.MessageExt;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static io.openmessaging.storage.dledger.store.file.DefaultMmapFile.clean;
import static io.openmessaging.storage.dledger.store.file.DefaultMmapFile.ensureDirOK;

/**
 * @author: xuxianbei
 * Date: 2021/1/23
 * Time: 11:05
 * Version:V1.0
 */
@Slf4j
@Data
public class MappedFile extends ReferenceResource {
    //操作系统每页大小，默认4K
    public static final int OS_PAGE_SIZE = 1024 * 4;
    //当前JVM实例中MappedFile虚拟内存
    private static final AtomicLong TOTAL_MAPPED_VIRTUAL_MEMORY = new AtomicLong(0);
    //当前JVM实例中MappedFile对象个数
    private static final AtomicInteger TOTAL_MAPPED_FILES = new AtomicInteger(0);
    //当前该文件写指针
    protected final AtomicInteger wrotePosition = new AtomicInteger(0);
    //当前文件提交指针
    protected final AtomicInteger committedPosition = new AtomicInteger(0);
    //刷写到磁盘指针
    private final AtomicInteger flushedPosition = new AtomicInteger(0);
    //文件大小
    protected int fileSize;
    private String fileName;
    //堆内存ByteBuffer
    protected ByteBuffer writeBuffer = null;
    //文件通道
    protected FileChannel fileChannel;
    //堆内存池
    protected TransientStorePool transientStorePool = null;
    //改文件初始偏移量
    private long fileFromOffset;
    private File file;
    //物理文件对应的内存映射buffer
    private MappedByteBuffer mappedByteBuffer;
    //文件最后一次内容写入时间
    private volatile long storeTimestamp = 0;
    //是否是MappedFileQueue队列第一个文件
    private boolean firstCreateInQueue = false;

    public MappedFile(final String fileName, final int fileSize) throws IOException {
        init(fileName, fileSize);
    }

    private void init(final String fileName, final int fileSize) throws IOException {
        this.file = new File(fileName);
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileFromOffset = Long.parseLong(this.file.getName());
        ensureDirOK(this.file.getParent());
        boolean ok = false;
        try {
            //通过RandomAccessFile创建读写文件通道
            this.fileChannel = new RandomAccessFile(this.file, "rw").getChannel();
            //并将文件内容使用NIO的内存映射Buffer将文件映射到内存中
            this.mappedByteBuffer = this.fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileSize);
            TOTAL_MAPPED_VIRTUAL_MEMORY.addAndGet(fileSize);
            TOTAL_MAPPED_FILES.incrementAndGet();
            ok = true;
        } catch (IOException e) {
            log.error("Failed to map file " + fileName, e);
            throw e;
        } finally {
            if (!ok && this.fileChannel != null) {
                this.fileChannel.close();
            }
        }
    }


    public boolean isFull() {
        return this.fileSize == this.wrotePosition.get();
    }

    public AppendMessageResult appendMessage(final MessageExtBrokerInner msg, final AppendMessageCallback cb) {
        return appendMessagesInner(msg, cb);
    }

    public AppendMessageResult appendMessagesInner(final MessageExt messageExt, final AppendMessageCallback cb) {
        assert messageExt != null;
        assert cb != null;
        int currentPos = this.wrotePosition.get();
        if (currentPos < this.fileSize) {
            ByteBuffer byteBuffer = writeBuffer != null ? writeBuffer.slice() : this.mappedByteBuffer.slice();
            byteBuffer.position(currentPos);
            AppendMessageResult result;
            if (messageExt instanceof MessageExtBrokerInner) {
                //然后拿到原来的文件的偏移指针，一个个添加上去
                result = cb.doAppend(this.getFileFromOffset(), byteBuffer, this.fileSize - currentPos, (MessageExtBrokerInner) messageExt);
            }
        }
        return null;
    }

    public int flush(final int flushLeastPages) {
        if (this.isAbleToFlush(flushLeastPages)) {
            if (this.hold()) {
                int value = getReadPosition();

            }
        }

        return 0;

    }

    public int commit(final int commitLeastPages) {
        if (writeBuffer == null) {
            return this.wrotePosition.get();
        }
        if (this.isAbleToCommit(commitLeastPages)) {
            if (this.hold()) {
                commit0(commitLeastPages);
                this.release();
            } else {
                log.warn("in commit, hold failed, commit offset = " + this.committedPosition.get());
            }
        }
        return 0;
    }

    private void commit0(int commitLeastPages) {
        int writePos = this.wrotePosition.get();
        int lastCommittedPosition = this.committedPosition.get();
        if (writePos - this.committedPosition.get() > 0) {
            try {
                ByteBuffer byteBuffer = writeBuffer.slice();
                byteBuffer.position(lastCommittedPosition);
                byteBuffer.limit(writePos);
                this.fileChannel.position(lastCommittedPosition);
                this.fileChannel.write(byteBuffer);
                this.committedPosition.set(writePos);
            } catch (Throwable e) {
                log.error("Error occurred when commit data to FileChannel.", e);
            }
        }
    }

    protected boolean isAbleToCommit(final int commitLeastPages) {
        int flush = this.committedPosition.get();
        int write = this.wrotePosition.get();

        if (this.isFull()) {
            return true;
        }

        if (commitLeastPages > 0) {
            return ((write / OS_PAGE_SIZE) - (flush / OS_PAGE_SIZE)) >= commitLeastPages;
        }

        return write > flush;

    }

    private boolean isAbleToFlush(final int flushLeastPages) {


        return true;
    }


    @Override
    public boolean cleanup(final long currentRef) {
        clean(this.mappedByteBuffer);
        TOTAL_MAPPED_VIRTUAL_MEMORY.addAndGet(this.fileSize * (-1));
        TOTAL_MAPPED_FILES.decrementAndGet();
        log.info("unmap file[REF:" + currentRef + "] " + this.fileName + " OK");
        return true;
    }


    public int getReadPosition() {
        return this.writeBuffer == null ? this.wrotePosition.get() : this.committedPosition.get();
    }
}

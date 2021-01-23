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

/**
 * @author: xuxianbei
 * Date: 2021/1/23
 * Time: 11:05
 * Version:V1.0
 */
@Slf4j
@Data
public class MappedFile extends ReferenceResource {

    protected int fileSize;
    protected ByteBuffer writeBuffer = null;
    protected FileChannel fileChannel;
    private long fileFromOffset;
    private File file;
    private MappedByteBuffer mappedByteBuffer;

    public MappedFile(final String fileName, final int fileSize) throws IOException {
        init(fileName, fileSize);
    }

    private void init(final String fileName, final int fileSize) throws IOException {
        this.file = new File(fileName);
        boolean ok = false;
        try {
            this.fileChannel = new RandomAccessFile(this.file, "rw").getChannel();
            this.mappedByteBuffer = this.fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileSize);
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


    protected final AtomicInteger wrotePosition = new AtomicInteger(0);

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
}

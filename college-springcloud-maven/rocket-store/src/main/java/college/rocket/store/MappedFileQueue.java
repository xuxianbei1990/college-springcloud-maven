package college.rocket.store;

import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2021/1/23
 * Time: 11:08
 * Version:V1.0
 */
@Data
public class MappedFileQueue {

    private final String storePath;
    private final int mappedFileSize;
    private final AllocateMappedFileService allocateMappedFileService;
    private long flushedWhere = 0;


    public MappedFile getLastMappedFile() {
        return null;
    }

    public MappedFile getLastMappedFile(final long startOffset) {
        return getLastMappedFile(startOffset, true);
    }

    public MappedFile getLastMappedFile(final long startOffset, boolean needCreate) {
        return null;
    }

    public MappedFileQueue(final String storePath, int mappedFileSize,
                           AllocateMappedFileService allocateMappedFileService) {
        this.storePath = storePath;
        this.mappedFileSize = mappedFileSize;
        this.allocateMappedFileService = allocateMappedFileService;
    }

    public boolean flush(final int flushLeastPages) {
        boolean result = true;
        MappedFile mappedFile = this.findMappedFileByOffset(this.flushedWhere, this.flushedWhere == 0);
        if (mappedFile != null) {
            long tmpTimeStamp = mappedFile.getStoreTimestamp();
            int offset = mappedFile.flush(flushLeastPages);
        }

        return false;
    }

    public MappedFile findMappedFileByOffset(final long offset, final boolean returnFirstOnNotFound) {
        return null;
    }

    public long getMaxOffset() {
        MappedFile mappedFile = getLastMappedFile();
        if (mappedFile != null) {
            return mappedFile.getFileFromOffset() + mappedFile.getReadPosition();
        }
        return 0;
    }

    public boolean load() {
        return false;
    }
}

package college.rocket.store;

/**
 * @author: xuxianbei
 * Date: 2021/1/23
 * Time: 11:08
 * Version:V1.0
 */
public class MappedFileQueue {

    private final String storePath;
    private final int mappedFileSize;
    private final AllocateMappedFileService allocateMappedFileService;

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
}

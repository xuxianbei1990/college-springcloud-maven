package college.rocket.store;

import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2021/1/23
 * Time: 10:55
 * Version:V1.0
 */
@Data
public class AppendMessageResult {

    // Return code
    private AppendMessageStatus status;
    // Where to start writing
    private long wroteOffset;
    // Write Bytes
    private int wroteBytes;
    // Message ID
    private String msgId;
    // Message storage timestamp
    private long storeTimestamp;
    // Consume queue's offset(step by one)
    private long logicsOffset;
    private long pagecacheRT = 0;

    public AppendMessageResult(AppendMessageStatus status) {
        this(status, 0, 0, "", 0, 0, 0);
    }

    public AppendMessageResult(AppendMessageStatus status, long wroteOffset, int wroteBytes, String msgId,
                               long storeTimestamp, long logicsOffset, long pagecacheRT) {
        this.status = status;
        this.wroteOffset = wroteOffset;
        this.wroteBytes = wroteBytes;
        this.msgId = msgId;
        this.storeTimestamp = storeTimestamp;
        this.logicsOffset = logicsOffset;
        this.pagecacheRT = pagecacheRT;
    }
}

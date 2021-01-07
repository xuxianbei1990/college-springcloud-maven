package college.rocketmq.client.consumer.exception;

/**
 * @author: xuxianbei
 * Date: 2020/12/30
 * Time: 15:54
 * Version:V1.0
 */
public class MQClientException extends Exception {
    private int responseCode;
    private String errorMessage;

    public MQClientException(String errorMessage, Throwable cause) {
//        super(FAQUrl.attachDefaultURL(errorMessage), cause);
        this.responseCode = -1;
        this.errorMessage = errorMessage;
    }

    public MQClientException(int responseCode, String errorMessage) {
//        super(FAQUrl.attachDefaultURL("CODE: " + UtilAll.responseCode2String(responseCode) + "  DESC: "
//                + errorMessage));
        this.responseCode = responseCode;
        this.errorMessage = errorMessage;
    }
}

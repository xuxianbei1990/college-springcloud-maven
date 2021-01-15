package college.rocketmq.client.exception;

import college.rocket.common.UtilAll;

/**
 * @author: xuxianbei
 * Date: 2021/1/14
 * Time: 14:09
 * Version:V1.0
 */
public class MQBrokerException extends Exception {

    private final int responseCode;
    private final String errorMessage;

    public MQBrokerException(int responseCode, String errorMessage) {
        super("CODE: " + UtilAll.responseCode2String(responseCode) + "  DESC: "
                + errorMessage);
        this.responseCode = responseCode;
        this.errorMessage = errorMessage;
    }
}

package college.springcloud.producter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author: xuxianbei
 * Date: 2020/9/15
 * Time: 13:30
 * Version:V1.0
 */
@Data
public class StudentVo {
    private String name;
    private Integer age;
    /**
     * createOrder, payOrder, Finished
     */
    private String orderType;

    @Getter
    public enum OrderTypeEnum {
        CREATE_ORDER("CO001", "创建订单"),
        PAY_ORDER("CO002", "支付订单"),
        FINISHED_ORDER("CO003", "完成订单");

        private String key;
        private String desc;

        OrderTypeEnum(String key, String desc) {
            this.key = key;
            this.desc = desc;
        }
    }
}

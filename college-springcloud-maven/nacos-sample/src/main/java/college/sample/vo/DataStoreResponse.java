package college.sample.vo;

import lombok.Data;

/**
 * 数仓响应
 * @author: xuxianbei
 * Date: 2021/8/5
 * Time: 15:07
 * Version:V1.0
 */
@Data
public class DataStoreResponse {
    /**
     * 0:成功
     */
    private Integer code = 0;

    /**
     * 加密字符串
     */
    private String encryptData;

    /**
     * 提示信息
     */
    private String msg;

}

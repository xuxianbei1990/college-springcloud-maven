package college.codegenerate.module;

import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2022/5/12
 * Time: 16:01
 * Version:V1.0
 */
@Data
public class CodeGenDTO {
    private String receiptType;
    private Integer count = 1;
    private String brandPrefix;
    private String brandCode;
    private Long tenantId;
}

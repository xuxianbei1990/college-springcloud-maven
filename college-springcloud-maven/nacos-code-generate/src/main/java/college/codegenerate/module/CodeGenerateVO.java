package college.codegenerate.module;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: xuxianbei
 * Date: 2021/3/10
 * Time: 10:13
 * Version:V1.0
 */
@Data
public class CodeGenerateVO implements Serializable {
//    @ApiModelProperty("编码")
    private String code;
//    @ApiModelProperty("品牌")
    private Boolean brand;
//    @ApiModelProperty("辅料")
    private Boolean accessories;
//    @ApiModelProperty("面料")
    private Boolean fabric;
//    @ApiModelProperty("公司")
    private Boolean company;

    public static CodeGenerateVO of() {
        CodeGenerateVO codeGenerateVO = new CodeGenerateVO();
        codeGenerateVO.setBrand(Boolean.FALSE);
        codeGenerateVO.setAccessories(Boolean.FALSE);
        codeGenerateVO.setFabric(Boolean.FALSE);
        codeGenerateVO.setCompany(Boolean.FALSE);
        codeGenerateVO.setCode("");
        return codeGenerateVO;
    }
}

package college.sample.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * author:   tangwei
 * Date:     2021/3/1 15:49
 * Description: 财务账户信息
 */
@Data
public class FinanceAccountInfoVO implements Serializable {

    private static final long serialVersionUID = 4305084710265812488L;

    /**
     * 账户信息表主键id---star_account_info
     */
    @ApiModelProperty("账户信息表主键id")
    private Long accountId;

    @ApiModelProperty("账户类型：1红人账户，2客户账户")
    private Integer accountType;

    /**
     * 收款户名---star_account_info
     */
    @ApiModelProperty(value = "收款户名", example = "1")
    private String accountName;

    /**
     * 收款卡号---star_account_info
     */
    @ApiModelProperty(value = "收款卡号")
    private String accountNumber;

    /**
     * 开户行---star_account_info
     */
    @ApiModelProperty(value = "开户行")
    private String accountBank;

    /**
     * 开户支行---star_account_info
     */
    @ApiModelProperty(value = "开户支行")
    private String accountBranchBank;

}
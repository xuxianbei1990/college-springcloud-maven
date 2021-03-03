package college.sample.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * author:   tangwei
 * Date:     2021/2/26 10:52
 * Description: 执行单结算信息
 */
@Data
public class ExcutionSettleInfoVO implements Serializable {

    private static final long serialVersionUID = -7798885665279854107L;

    @ApiModelProperty("执行单主键id")
    private Long id;

    /**
     * 执行单号
     */
    @ApiModelProperty("执行单号")
    private String excuteCode;

    @ApiModelProperty("执行单类型(1-内部红人执行单，2-外部红人执行单)")
    private Integer type;

    /**
     * star_platform_info表主键id
     */
    @ApiModelProperty("star_platform_info表主键id")
    private Long starPlatformInfoId;

    @ApiModelProperty("红人id")
    private Long starId;

    /**
     * 红人昵称
     */
    @ApiModelProperty("红人昵称")
    private String starNickName;

    /**
     * 本单分成比例
     */
    @ApiModelProperty("本单分成比例")
    private BigDecimal dividedRate;

    /**
     * 执行人id
     */
    @ApiModelProperty("执行人id")
    private Long excutorId;

    /**
     * 执行人名称
     */
    @ApiModelProperty("执行人名称")
    private String excutorName;

    /**
     * 实际发布日期---excute_feedback
     */
    @ApiModelProperty("实际发布日期")
    private LocalDateTime realPublishDate;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createDate;

    /**
     * 创建人名称
     */
    @ApiModelProperty("创建人名称")
    private String createName;

    /**
     * 创建人id
     */
    @ApiModelProperty("创建人id")
    private Long createBy;

    @ApiModelProperty("创建人所属部门")
    private String creatorDepartment;

    /**
     * 我司合同主体(数据字典)---income_contract
     */
    @ApiModelProperty("我司合同签约抬头-数据字典")
    private String companyContractSubject;
    
    @ApiModelProperty("我司合同签约抬头-中文值")
    private String companyContractSubjectName;

    /**
     * 客户签约主体---income_contract
     */
    @ApiModelProperty("客户")
    private String customerSignSubject;

    /**
     * 本人姓名---star_basic_data
     */
    @ApiModelProperty(value = "红人本名")
    private String starName;

    /**
     * 红人手机号---star_account_info
     */
    @ApiModelProperty(value = "红人手机号")
    private String starPhone;

    /**
     * 红人个税---star_account_info
     */
    @ApiModelProperty(value = "红人个税", example = "1.0")

    private BigDecimal taxRate;

    /**
     * AE确认实际报价(默认值执行单实际报价)---calculation_devided
     */
    @ApiModelProperty("实际报价")
    private BigDecimal aeRealAmount;

    /**
     * AE确认客户返点比例---calculation_devided
     */
    @ApiModelProperty("AE确认客户返点比例")
    private BigDecimal aeCustomerRabateRate;

    /**
     * AE确认分成金额---calculationDevided
     */
    @ApiModelProperty("商务确认分成金额（不含税）")
    private BigDecimal aeDevidedAmount;

    /**
     * 财务确认分成金额
     */
    @ApiModelProperty("财务核准分成金额")
    private BigDecimal financeDevidedAmount;

    /**
     * 红人固定到手价
     */
    @ApiModelProperty("红人固定到手价")
    private BigDecimal handPrice;

    /**
     * 实际打款金额=AE确认分成金额*（1+手续费率），如果非第三方平台打款，手续费率为0
     */
    /*@ApiModelProperty("实际打款金额")
    private BigDecimal actualPayAmount;*/

    /**
     * 红人账户信息表主键id---star_account_info
     */
    @ApiModelProperty("红人账户信息表主键id")
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

    /**
     * 红人开户省
     */
    @ApiModelProperty("红人开户省")
    private String accountProvince;

    /**
     * 红人开户市
     */
    @ApiModelProperty("红人开户市")
    private String accountCity;

    /**
     * 流程节点id
     */
    @ApiModelProperty(value = "流程节点id", example = "1")
    private Long flowId;

    /**
     * 流程id返回
     */
    @ApiModelProperty(value = "审批流程")
    private String flowIds;

    /**
     * 抬头
     */
    @ApiModelProperty("抬头")
    private String header;

    /**
     * 标题
     */
    @ApiModelProperty("标题")
    private String title;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;

    /**
     * 红人采购费，取执行单的【本单固定到手价】或者采购合同的合同金额；年度返点费，取年度返点申请单的【确认返点金额】；客户返点费，执行单里面的【AE确认客户返点金额】
     */
    @ApiModelProperty("借款金额")
    private BigDecimal loanAmount;
}
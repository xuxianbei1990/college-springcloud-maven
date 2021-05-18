package college.springcloud.producter.utils;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: xuxianbei
 * Date: 2021/5/12
 * Time: 18:22
 * Version:V1.0
 */
@Data
public class CfInvoiceCommonExportVo {

    /**
     * 帐单号
     */
    @Excel(name = "帐单号")
    private String invoiceNo;

    /**
     * 账单状态（账单状态 1待提交；2待结算；3部分结算；4全部结算；7已作废，0已删除,5 业务待审核,8 待提交财务,9已核销,10审批中,11待打款,12已开票，13已撤回, 14审批拒绝, 15打款中'
     */
    @Excel(name = "账单状态", replace = {"待提交_1", "待结算_2", "部分结算_3", "全部结算_4", "已作废_7", "已删除_0", "业务待审核_5", "待提交财务_8", "已核销_9",
            "审批中_10", "待打款_11", "已开票_12", "已撤回_13", "审批拒绝_14", "打款中_15"})
    private Integer invoiceStatus;

    /**
     * 业务类型(货品采购1; 销售订单2; 3:MCN)
     */
    @Excel(name = "业务类型", replace = {"货品采购_1", "销售订单_2", "MCN_3"})
    private String jobType;

    /**
     * 应收/应付类型  AR=收；AP=付；
     */
    @Excel(name = "收付类型", replace = {"收_AR", "付_AP"})
    private String invoiceType;

    /**
     * 帐单金额-应付总金额
     */
    @Excel(name = "账单金额")
    private BigDecimal invoicelCredit;

    /**
     * 结算主体
     */
    @Excel(name = "结算主体")
    private String balance;

    /**
     * 我司打款户名
     */
    @Excel(name = "我司打款户名")
    private String accountName;

    /**
     * 我司打款账号
     */
    @Excel(name = "我司打款账号")
    private String cardNumber;

    /**
     * 我司打款银行
     */
    @Excel(name = "我司打款银行")
    private String bankName;

    /**
     * 来源单号
     */
    @Excel(name = "来源单号")
    private String chargeSourceCode;

    /**
     * 费用名称
     * 费用种类 1=红人分成费  2=客户返点费 3=MCN收入 4=红人采购费 5=年度返点费 6 = 平台手续费
     */
    private String chargeType;


    /**
     * 财务核准分成金额
     */
    @Excel(name = "财务核准分成金额")
    private BigDecimal financeDevidedAmount;

    /**
     * 收款户名  结算用的
     */
    @Excel(name = "收款户名")
    private String mcnAccountName;

    /**
     * 身份证号码
     */
    @Excel(name = "身份证号码")
    private String accountIdCard;

    /**
     * 收款卡号
     */
    @Excel(name = "收款卡号")
    private String accountNumber;


    /**
     * 开户行
     */
    @Excel(name = "开户行")
    private String accountBank;

    /**
     * 开户支行
     */
    @Excel(name = "开户支行")
    private String accountBranchBank;

    /**
     * 红人开户省
     */
    @Excel(name = "开户省")
    private String accountProvince;

    /**
     * 红人开户市
     */
    @Excel(name = "开户市")
    private String accountCity;

    /**
     * 手机号
     */
    @Excel(name = "手机号")
    private String accountPhone;

    /**
     * 品牌名称
     */
    @Excel(name = "品牌名称")
    private String brandName;

    /**
     * 第三方打款平台户名--打款户名
     */
    @Excel(name = "第三方打款平台户名")
    private String thirdPaymentAccount;

    /**
     * 第三方打款平台名称 --打款平台名称
     */
    @Excel(name = "第三方打款平台名称")
    private String thirdAccountPlatform;

    /**
     * 第三方打款银行-打款银行
     */
    @Excel(name = "第三方打款银行")
    private String thirdAccountBank;

    /**
     * 第三方打款平台账号-账号
     */
    @Excel(name = "第三方打款平台账号")
    private String thirdAccountNumber;


}

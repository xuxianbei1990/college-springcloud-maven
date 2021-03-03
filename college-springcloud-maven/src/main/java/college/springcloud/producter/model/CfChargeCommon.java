package college.springcloud.producter.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 费收_费用(cf_charge_common）
 * </p>
 *
 * @author admin
 * @since 2021-02-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cf_charge_common")
public class CfChargeCommon implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 费用内部编号id
     */
    @TableId(value = "charge_id", type = IdType.AUTO)
    private Long chargeId;

    /**
     * 费用号
     */
    private String chargeCode;

    /**
     * 1= MCN
     */
    private Integer chargeSourceType;

    /**
     * 1=红人分成费  2=客户返点费 3=MCN收入 4=红人采购费 5=年度返点费 6 = 平台手续费
     */
    private Integer chargeType;

    /**
     * 费用审核状态 1草稿、2已提交、3已审核、4已驳回、5已作废、0已删除
     */
    private Integer checkStatus;

    /**
     * AR=收、AP=付；
     */
    private String arapType;

    /**
     * 费用来源单号:1.MCN收入取收入合同编号SR20201231675;2.红人分成费,客户返点费,红人采购费:取执行单号：ZXD20201231675;3.年度返点费 取年度返点申请单号
     */
    private String chargeSourceCode;

    /**
     * 费用来源明细: 默认取值等于来源单号，其中MCN收入费用记执行单号
     */
    private String chargeSourceDetail;

    /**
     * 币种
     */
    private String currencyCode;

    /**
     * 汇率
     */
    private BigDecimal exchangeRate;

    /**
     * 税率
     */
    private BigDecimal taxRate;

    /**
     * 计费单位
     */
    private String chargeUnit;

    /**
     * 数量
     */
    private Integer chargeQty;

    /**
     * 单价(pp)
     */
    private BigDecimal pricePp;

    /**
     * 总价(pp)
     */
    private BigDecimal amountPp;

    /**
     * 结算主体
     */
    private String balance;

    /**
     * 财务主体取值 业务单据 我司签约主体 字段
     */
    private String financeEntity;

    /**
     * 自动生成的按@createdate留空；手动创建费用时界面填入(格式:4位数字年,2位数字月)
     */
    private LocalDateTime chargeMonthBelongTo;

    /**
     * 发票号-费用开票后反写；
     */
    private String taxInvoiceNo;

    /**
     * 发票日期
     */
    private LocalDateTime taxInvoiceDate;

    /**
     * 帐单号
     */
    private String invoiceNo;

    /**
     * 帐单抬头
单据生成费用时，直接写入费用表1.MCN收入：客户开票抬头2.客户返点费：客户开票抬头3.执行单：收款户名
     */
    private String invoiceTitle;

    /**
     * 帐单抬头名称 默认取值账单抬头
     */
    private String invoiceTitleName;

    /**
     * 帐单日期
     */
    private LocalDateTime invoiceDate;

    /**
     * 核销流水号
     */
    private String clearNo;

    /**
     * 实收金额(核销后反写)
     */
    private BigDecimal actualAmount;

    /**
     * 实收日期
     */
    private LocalDateTime actualDate;

    /**
     * 实收历史日期
     */
    private String actualHistoryDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 应收日期
     */
    private LocalDateTime chargeDate;

    /**
     * 入账时间
     */
    private LocalDateTime invoiceEntranceDate;

    /**
     * 结算模板：1=内部红人执行单模板
，2= 外部红人执行单模板，3= 红人采购费/年度返点/客户返点模板
     */
    private Integer settTemplate;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 创建人名称
     */
    private String createName;

    /**
     * 创建日期
     */
    private LocalDateTime createDate;

    /**
     * 公司
     */
    private Long companyId;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 更新人
     */
    private Long updateBy;

    /**
     * 更新人名称
     */
    private String updateName;

    /**
     * 更新日期
     */
    private LocalDateTime updateDate;


}

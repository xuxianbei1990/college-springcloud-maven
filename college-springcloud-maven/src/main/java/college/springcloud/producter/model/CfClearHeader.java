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
 * 财务_核销(cf_clear_header）
 * </p>
 *
 * @author lywang
 * @since 2020-08-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cf_clear_header")
public class CfClearHeader implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "clear_id", type = IdType.AUTO)
    private Long clearId;

    /**
     * 核销单号
     */
    private String clearNo;


    /**
     * 帐单编号
     */
    private String invoiceNo;

    /**
     * 核销方式(0=转帐; 1=现金; 2=支票，）
     */
    private String clearMethod;

    /**
     * 状态(1=未核销， 2=已核销,0=已删除)
     */
    private Integer clearStatus;

    /**
     * 收付时间
     */
    private LocalDateTime actualArApDate;

    /**
     * 品牌
     */
    private Long brandId;

    /**
     * 结算主体
     */
    private String balance;

    /**
     * 财务人员
     */
    private String fiUser;

    /**
     * 银行
     */
    private String bank;

    /**
     * 银行帐号
     */
    private String bankNo;

    /**
     * 实收付款金额
     */
    private BigDecimal bankAmount;

    /**
     * 支票号
     */
    private String checkNo;

    /**
     * 核销币种
     */
    private String currencyCode;

    /**
     * 核销汇率
     */
    private BigDecimal exchangeRate;

    /**
     * 核销时间
     */
    private LocalDateTime clearDate;

    /**
     * 核销人id
     */
    private Long clearBy;

    /**
     * 费用总计_debit
     */
    private BigDecimal clearDebit;

    /**
     * 费用总计_credit
     */
    private BigDecimal clearCredit;

    /**
     * 费用总计类型
     */
    private String clearType;

    /**
     * 费用总计_balance
     */
    private BigDecimal clearBalance;

    /**
     * 本次核销总计_debit
     */
    private BigDecimal nowClearDebit;

    /**
     * 本次核销总计_credit
     */
    private BigDecimal nowClearCredit;

    /**
     * 本次核销总计类型(0=debit; 1=credit)
     */
    private String nowClearType;

    /**
     * 本次核销总计_balance
     */
    private BigDecimal nowClearBalance;

    /**
     * 上次核销余额_debit
     */
    private BigDecimal lastBalanceDebit;

    /**
     * 上次核销余额_credit
     */
    private BigDecimal lastBalanceCredit;

    /**
     * 上次核销余额类型(0=debit; 1=credit)
     */
    private String lastBalanceType;

    /**
     * 上次核销余额_balance
     */
    private BigDecimal lastBalanceBalance;

    /**
     * 本次核销余额_debit
     */
    private BigDecimal nowBalanceDebit;

    /**
     * 本次核销余额_credit
     */
    private BigDecimal nowBalanceCredit;

    /**
     * 本次核销余额类型(0=debit; 1=credit)
     */
    private String nowBalanceType;

    /**
     * 本次核销余额_balance
     */
    private BigDecimal nowBalanceBalance;

    /**
     * 备注
     */
    private String remark;

    /**
     * 公司
     */
    private Long companyId;

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

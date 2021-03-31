package college.sample.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author tangwei
 * Date:     2021/1/19 21:02
 * Description: 收入合同
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class IncomeContract implements Serializable {

    private static final long serialVersionUID = 928666789517873605L;

    private Long id;

    /**
     * 合同编号
     */
    @ApiModelProperty("合同编号")
    private String contractCode;

    /**
     * 合同类型
     *
     */
    @NotNull
    private Integer contractType;

    private String contractTypeName;

    /**
     * 客户id
     */
    @ApiModelProperty("客户id")
    @NotNull
    private Long customerId;

    /**
     * 是否含直播全案
     */
    @ApiModelProperty("是否含直播全案")
    @NotNull
    private Boolean liveBroadcastStatus;

    /**
     * 折扣审批单主键id
     */
    @ApiModelProperty("折扣审批单主键id")
    private Long discountId;

    /**
     * 折扣审批单编号
     */
    @ApiModelProperty("折扣审批单编号")
    private String discountCode;

    /**
     * 年框合同主键id
     */
    @ApiModelProperty("年框合同主键id")
    private Long yearContractId;

    /**
     * 年框合同编号
     */
    @ApiModelProperty("年框合同编号")
    private String yearContractCode;

    /**
     * 刊例总报价
     */
    @ApiModelProperty("刊例总报价")
    @NotNull
    private BigDecimal totalAmount;

    /**
     * 审批状态:0_待提交、1_审批中、2_审批通过、3_审批拒绝、4_已撤回、9_已作废。
     */
    @ApiModelProperty("审批状态:0_待提交、1_审批中、2_审批通过、3_审批拒绝、4_已撤回、9_已作废。")
    private Integer approvalStatus;

    private String approvalStatusName;

    /**
     * 折扣比例
     */
    @ApiModelProperty("折扣比例")
    private BigDecimal discountRate;

    /**
     * 折扣后合同金额=刊例总报价*折扣比例
     */
    @ApiModelProperty("折扣后合同金额=刊例总报价*折扣比例")
    @NotNull
    private BigDecimal discountAmount;

    /**
     * 商务确认合同金额
     */
    @ApiModelProperty("商务确认合同金额")
    @NotNull
    private BigDecimal businessAmount;

    @ApiModelProperty("最新合同金额")
    private BigDecimal newestAmount;

    /**
     * 形式合同类型(数据字典)
     */
    @ApiModelProperty("形式合同类型(数据字典)")
    private String formalContractType;

    /**
     * 平台下单截图
     */
    @ApiModelProperty("平台下单截图")
    private String orderScreenshot;

    /**
     * 平台订单号
     */
    @ApiModelProperty("平台订单号")
    private String platformOrderNumber;

    /**
     * 我司合同主体(数据字典)
     */
    @ApiModelProperty("我司合同主体(数据字典)")
    @NotNull
    private String companyContractSubject;

    @ApiModelProperty("客户开票信息id")
    private Long customerBillingId;

    /**
     * 客户签约主体
     */
    @ApiModelProperty("客户签约主体")
    @NotNull
    private String customerSignSubject;

    /**
     * 合同签订日期（精确到天）
     */
    @ApiModelProperty("合同签订日期（精确到天）")
    @NotNull
    private LocalDateTime signDate;

    /**
     * 策划组是否参与
     */
    @ApiModelProperty("策划组是否参与")
    @NotNull
    private Boolean plannerParticipate;

    /**
     * 合同来源（数据字典）
     */
    @ApiModelProperty("合作来源（数据字典）")
    @NotNull
    private String cooperateSource;

    /**
     * 合作品牌
     */
    @ApiModelProperty("合作品牌")
    @NotNull
    private String cooperateBrand;

    /**
     * 合作产品
     */
    @ApiModelProperty("合作品牌")
    @NotNull
    private String cooperateProduct;

    /**
     * 上传合同
     */
    @ApiModelProperty("上传合同")
    @NotNull
    private String contractFile;

    /**
     *  是否是全案合同
     */
    @ApiModelProperty("是否是全案合同")
    private Boolean broadcastContract;

    /**
     * 计划回款日期（精确到天）
     */
    @ApiModelProperty("计划回款日期（精确到天）")
    private LocalDateTime plannedPaymentDate;

    /**
     * 是否为平台代下单
     */
    @ApiModelProperty("是否为平台代下单")
    private Boolean platformOrders;

    /**
     * 合作内容简介
     */
    @ApiModelProperty("合作内容简介")
    private String cooperateContent;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 删除标记
     */
    @ApiModelProperty("删除标记")
    private Integer isDelete;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 创建人名称
     */
    private String createName;

    /**
     * 创建人id
     */
    private Long createBy;

    /**
     * 修改时间
     */
    private Date updateDate;

    /**
     * 修改人id
     */
    private Long updateBy;

    /**
     * 修改人名称
     */
    private String updateName;

    /**
     * 部门id
     */
    private Long departmentId;

    /**
     * 公司id
     */
    private Long companyId;

    /**
     * 租户id
     */
    private Long tenantId;
}
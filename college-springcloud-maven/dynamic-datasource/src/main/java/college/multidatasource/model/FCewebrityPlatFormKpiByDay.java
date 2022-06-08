package college.multidatasource.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 多图片存储
 * </p>
 *
 * @author xuxianbei
 * @since 2021-05-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("f_cewebrity_platform_kpi_by_day")
public class FCewebrityPlatFormKpiByDay implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 普通帐单内部编号
     */
    private Integer platformId;

    /**
     * 业务ID
     */
    private String platformUserId;

    /**
     * 类型1:账单
     */
    private Integer dateId;

    /**
     * 合同url
     */
    private int pFansNum;


}

package college.sample.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 执行单回调已打款
 * @author: xuxianbei
 * Date: 2021/3/8
 * Time: 16:39
 * Version:V1.0
 */
@Data
public class ExcutionSetPaidDTO implements Serializable {

    @ApiModelProperty("费用来源单号（执行单号）")
    @NotNull
    private String excuteCode;

    @ApiModelProperty("打款备注")
    @NotNull
    private String paidRemark;

    @ApiModelProperty("打款日期")
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paidDate;
}
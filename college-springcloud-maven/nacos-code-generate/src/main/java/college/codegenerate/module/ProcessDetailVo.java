package college.codegenerate.module;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author pengjian
 * @date 2021/1/25
 * 执行过程
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessDetailVo {

    /**
     * 执行过程的Id,修改或者查询的需要
     */
    private Long id;

    /**
     * 是否是多样,false不是，true是默认值不是
     */
    private Boolean multiple;

    /**
     * 父级的Id,默认为0
     */
    private Long parentId;

    /**
     * 执行的任务的Id
     */
    private Long processId;

    /**
     *过程的状态
     */
    private Integer resultType;

    /**
     * 状态值
     */
    private String resultTypeValue;

    /**
     * 执行人的用户ID
     */
    private String executorUserId;

    /**
     * 执行人的名称
     */
    private String executorUserName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 意见
     */
    private String remark;

}

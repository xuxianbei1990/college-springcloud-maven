package college.springcloud.producter.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2020/9/15
 * Time: 13:30
 * Version:V1.0
 */
@Data
@AllArgsConstructor
public class StudentVo {
    private String name;
    private Integer age;
}

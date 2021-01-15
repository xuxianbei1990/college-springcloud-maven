package college.rocket.remoting.common;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2021/1/8
 * Time: 17:01
 * Version:V1.0
 */
@Data
@AllArgsConstructor
public class Pair<T1, T2> {
    private T1 object1;
    private T2 object2;
}

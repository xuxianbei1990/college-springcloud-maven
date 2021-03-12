package college.codegenerate.module;

import java.io.Serializable;

/**
 * @author: xuxianbei
 * Date: 2021/3/10
 * Time: 10:11
 * Version:V1.0
 */
public interface IResultCode extends Serializable {
    String getMessage();

    int getCode();
}

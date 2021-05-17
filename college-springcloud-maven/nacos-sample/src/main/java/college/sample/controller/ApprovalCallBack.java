package college.sample.controller;

import java.io.Serializable;

/**
 * 状态
 *
 * @author: xuxianbei
 * Date: 2021/4/22
 * Time: 19:39
 * Version:V1.0
 */
public interface ApprovalCallBack extends Serializable {

    void refuse();

    void success();
}

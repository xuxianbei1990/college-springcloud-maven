package college.sample.controller;

/**
 * @author: xuxianbei
 * Date: 2021/4/22
 * Time: 20:54
 * Version:V1.0
 */
public class ApprovalCallBackImpl implements ApprovalCallBack {
    @Override
    public void refuse() {
        System.out.println(1);
    }

    @Override
    public void success() {
        System.out.println("success");
    }
}

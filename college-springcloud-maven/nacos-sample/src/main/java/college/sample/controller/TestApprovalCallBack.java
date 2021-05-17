package college.sample.controller;

import com.alibaba.fastjson.JSONObject;

/**
 * @author: xuxianbei
 * Date: 2021/4/22
 * Time: 20:53
 * Version:V1.0
 */
public class TestApprovalCallBack {

    public static void main(String[] args) {
//        ApprovalCallBackImpl approvalCallBack = new ApprovalCallBackImpl();
        String str = JSONObject.toJSONString(new ApprovalCallBack() {
            @Override
            public void refuse() {
                System.out.println(1);
            }

            @Override
            public void success() {
                System.out.println(1);
            }
        });

        ApprovalCallBack approvalCallBack1 = JSONObject.parseObject(str, ApprovalCallBack.class);
        approvalCallBack1.success();
    }
}

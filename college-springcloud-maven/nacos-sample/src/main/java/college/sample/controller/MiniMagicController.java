package college.sample.controller;

import college.sample.vo.DataStoreResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: xuxianbei
 * Date: 2021/8/6
 * Time: 11:27
 * Version:V1.0
 */
@RestController
public class MiniMagicController {

    @GetMapping(value = "/idc-atom-magic/dc_mcn_fans")
    DataStoreResponse getDcMcnFans(@RequestParam Integer platformId, @RequestParam String platformUserId, @RequestHeader("app_key") String appkey,
                                   @RequestHeader("Authorization") String token) {
        System.out.println(platformId + platformUserId + appkey + token);
        return new DataStoreResponse();
    }

    /**
     * 获取MCN红人生态
     *
     * @param platformId
     * @return
     */
    @GetMapping(value = "/idc-atom-magic/dc_mcn_red_quota")
    DataStoreResponse getDcMcnQuota(@RequestParam Integer platformId, @RequestParam String platformUserId, @RequestHeader("app_key") String appkey,
                                    @RequestHeader("Authorization") String token) {
        System.out.println(platformId + platformUserId + appkey + token);
        return new DataStoreResponse();
    }
}

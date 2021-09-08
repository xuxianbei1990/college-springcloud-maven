package college.processs.controller;

import college.processs.dto.ApprovalMapDTO;
import college.processs.module.ProcessDetailVo;
import college.processs.module.Response;

import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author: xuxianbei
 * Date: 2021/3/9
 * Time: 16:26
 * Version:V1.0
 */
@RestController
@RequestMapping("approval/")
public class ApprovalController {

    @PostMapping("/all-nodes")
    public Response<List<ProcessDetailVo>> getAllProcessDetailList(@RequestBody ApprovalMapDTO approval) {
        return new Response(0, "success", new ArrayList());
    }

    @PostMapping("{detal}")
    public Response<String> starProcess(@RequestPart String detal) {
        System.out.println(detal);
        return new Response(0, "success", String.valueOf(new Random().nextInt()));
    }

    @PostMapping("processing")
    public Response<Map<Long, String>> approvalprocessing(@RequestBody ApprovalMapDTO approvalMap) {
        Map<Long, String> map = new HashMap<>();
        approvalMap.getApprovalIdList().forEach(key -> map.put(key, "待张晶晶审批"));
        return new Response(0, "success", map);
    }
}

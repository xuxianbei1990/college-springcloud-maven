package college.processs.controller;

import college.processs.dto.ApprovalMapDTO;
import college.processs.module.ProcessDetailVo;
import college.processs.module.Response;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: xuxianbei
 * Date: 2021/3/9
 * Time: 16:26
 * Version:V1.0
 */
@RestController
@RequestMapping("/approval")
public class ApprovalController {

    @PostMapping("/all-nodes")
    public Response<List<ProcessDetailVo>> getAllProcessDetailList(@RequestBody ApprovalMapDTO approval) {
        return new Response(0, "success", new ArrayList());
    }
}

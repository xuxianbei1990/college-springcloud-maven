package college.sample.controller;

import college.sample.vo.Res;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: xuxianbei
 * Date: 2021/3/12
 * Time: 10:54
 * Version:V1.0
 */
@RestController
@RequestMapping("/annual/rebate")
public class AnnualRebateController {

    @GetMapping(value = "/finance/status")
    public Res<Object> financeStatusChange(@RequestParam List<String> rebateContractCodes, @RequestParam Integer status) {
        System.out.println(rebateContractCodes);
        System.out.println(status);
        return Res.ok(1);
    }

}

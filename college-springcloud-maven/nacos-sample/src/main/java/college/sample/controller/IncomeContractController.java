package college.sample.controller;

import college.sample.vo.IncomeContract;
import college.sample.vo.Response;
import io.swagger.models.auth.In;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: xuxianbei
 * Date: 2021/3/22
 * Time: 14:24
 * Version:V1.0
 */
@RestController
@RequestMapping("/incomeContract")
public class IncomeContractController {

    @GetMapping("/incomeContract/getByCode")
    Response<IncomeContract> getByCode(@RequestParam String contractCode) {
        IncomeContract incomeContract = new IncomeContract();
        incomeContract.setCustomerBillingId(1013L);
        return Response.ok(incomeContract);
    }
}

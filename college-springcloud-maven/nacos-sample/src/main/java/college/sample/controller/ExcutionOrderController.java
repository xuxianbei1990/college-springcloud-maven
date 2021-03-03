package college.sample.controller;

import college.sample.vo.ExcutionSettleInfoVO;
import college.sample.vo.FinanceAccountInfoVO;
import college.sample.vo.Res;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: xuxianbei
 * Date: 2021/3/2
 * Time: 16:22
 * Version:V1.0
 */
@RestController
@RequestMapping("/excutionOrder")
public class ExcutionOrderController {

    //    @ApiOperation("财务结算单的执行单信息")
    @GetMapping("/settleInfo")
    public Res<ExcutionSettleInfoVO> getSettleInfo(@RequestParam Integer chargeType, @RequestParam String chargeSourceCode) {
        return Res.ok(new ExcutionSettleInfoVO());
    }

    //    @ApiOperation("财务查询账户信息")
    @GetMapping("/accountInfo")
    public Res<FinanceAccountInfoVO> getFinanceAccount(@RequestParam Integer chargeType, @RequestParam String chargeSourceCode, @RequestParam String chargeSourceDetail) {
        FinanceAccountInfoVO financeAccountInfoVO = new FinanceAccountInfoVO();
        financeAccountInfoVO.setAccountBank("xxb");
        financeAccountInfoVO.setAccountNumber("332315502151245612");
        return Res.ok(financeAccountInfoVO);
    }
}

package college.sample.controller;

import college.sample.dto.ExcutionSetPaidDTO;
import college.sample.vo.ExcutionSettleInfoVO;
import college.sample.vo.FinanceAccountInfoVO;
import college.sample.vo.Res;
import org.springframework.web.bind.annotation.*;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

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

        ExcutionSettleInfoVO excutionSettleInfoVO = new ExcutionSettleInfoVO();
        excutionSettleInfoVO.setExcuteCode("哥斯拉");
        excutionSettleInfoVO.setType(1);
        excutionSettleInfoVO.setStarPlatformInfoId(1L);
        excutionSettleInfoVO.setStarId(1L);
        excutionSettleInfoVO.setStarNickName("哥斯拉");
        excutionSettleInfoVO.setDividedRate(BigDecimal.ONE);
        excutionSettleInfoVO.setExcutorId(1L);
        excutionSettleInfoVO.setExcutorName("哥斯拉");
        excutionSettleInfoVO.setRealPublishDate(LocalDateTime.now());
        excutionSettleInfoVO.setCreateDate(new Date());
        excutionSettleInfoVO.setCreateName("哥斯拉");
        excutionSettleInfoVO.setCreateBy(1L);
        excutionSettleInfoVO.setCreatorDepartment("气不气还是哥斯拉");
        excutionSettleInfoVO.setCompanyContractSubject("死亡射线");
        excutionSettleInfoVO.setCompanyContractSubjectName("奇美拉");
        excutionSettleInfoVO.setCustomerSignSubject("卢卡尔");
        excutionSettleInfoVO.setStarName("冰霜巨龙");
        excutionSettleInfoVO.setStarPhone("12341315354");
        excutionSettleInfoVO.setTaxRate(new BigDecimal("0.32"));
        excutionSettleInfoVO.setAeRealAmount(BigDecimal.valueOf(100000L));
        excutionSettleInfoVO.setAeCustomerRabateRate(new BigDecimal("0.23"));
        excutionSettleInfoVO.setAeDevidedAmount(new BigDecimal("100"));
        excutionSettleInfoVO.setFinanceDevidedAmount(new BigDecimal("101"));
        excutionSettleInfoVO.setHandPrice(new BigDecimal("31"));
        excutionSettleInfoVO.setAccountId(1L);
        excutionSettleInfoVO.setAccountType(1);
        excutionSettleInfoVO.setAccountName("哥斯拉喷死他");
        excutionSettleInfoVO.setAccountNumber("哥斯拉下段攻击");
        excutionSettleInfoVO.setAccountBank("哥斯拉中位攻击");
        excutionSettleInfoVO.setAccountBranchBank("哥斯拉上位攻击");
        excutionSettleInfoVO.setAccountProvince("哥斯拉");
        excutionSettleInfoVO.setAccountCity("红人开户市");
        excutionSettleInfoVO.setTitle("标题");
        excutionSettleInfoVO.setDescription("描述");
        excutionSettleInfoVO.setLoanAmount(new BigDecimal("23"));
        excutionSettleInfoVO.setSettleTemplate(1);
        return Res.ok(excutionSettleInfoVO);
    }

    //    @ApiOperation("财务查询账户信息")
    @GetMapping("/accountInfo")
    public Res<FinanceAccountInfoVO> getFinanceAccount(@RequestParam Integer chargeType, @RequestParam String chargeSourceCode, @RequestParam String chargeSourceDetail) {
        FinanceAccountInfoVO financeAccountInfoVO = new FinanceAccountInfoVO();
        financeAccountInfoVO.setAccountBank("xxb");
        financeAccountInfoVO.setAccountNumber("332315502151245612");
        return Res.ok(financeAccountInfoVO);
    }

    @GetMapping("/setPaid")
    Res<Object> setPaid(@RequestBody List<ExcutionSetPaidDTO> excutionSetPaidDTOS) {
        System.out.println(excutionSetPaidDTOS.toString());
        return Res.ok(1);
    }

}

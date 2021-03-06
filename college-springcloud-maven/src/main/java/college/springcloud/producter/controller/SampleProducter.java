package college.springcloud.producter.controller;

import college.springcloud.producter.mapper.CfClearHeaderMapper;
import college.springcloud.producter.model.CfClearHeader;
import college.springcloud.producter.model.SampleVo;
import college.springcloud.producter.service.ClearHeaderService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author: xuxianbei
 * Date: 2020/8/31
 * Time: 21:11
 * Version:V1.0
 */
@RequestMapping("/test")
@RestController
public class SampleProducter {

    @Resource
    CfClearHeaderMapper cfClearHeaderMapper;

    @Autowired
    ClearHeaderService clearHeaderService;

    @GetMapping("sample")
    public SampleVo sample() {
        return new SampleVo();
    }

    @GetMapping("table")
    public CfClearHeader gettable() {
        return cfClearHeaderMapper.selectById(1);
    }

    @GetMapping("batch/insert")
    public Integer batchInsert() {
        List<CfClearHeader> list =
                cfClearHeaderMapper.selectList(Wrappers.lambdaQuery(CfClearHeader.class).in(CfClearHeader::getClearId, Arrays.asList(1, 2, 3)));
        list.stream().forEach(key -> key.setClearId(null));
        return cfClearHeaderMapper.batchInsert(list);
    }

    @GetMapping("batch/update")
    public Integer batchUpdate() {
        List<CfClearHeader> list =
                cfClearHeaderMapper.selectList(Wrappers.lambdaQuery(CfClearHeader.class).in(CfClearHeader::getClearId, Arrays.asList(1, 2, 3)));
        list.stream().forEach(t -> t.setInvoiceNo("INV2009070002"));
        return cfClearHeaderMapper.batchUpdateById(list);
    }


    @GetMapping("transaction/rollback")
    public Integer transactionRollback() {
        return clearHeaderService.transactionRollback();
    }

    /**
     * 测试编程式事务
     * @return
     */
    @GetMapping("transaction/code")
    public Integer transactionCode() {
        return clearHeaderService.transactionCode();
    }

    /**
     * 测试内部方法事务生效
     * @return
     */
    @GetMapping("transaction/aop")
    public Integer transactionAop() {
        return clearHeaderService.transactionAop();
    }

}

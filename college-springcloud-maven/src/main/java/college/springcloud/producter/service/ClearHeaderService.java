package college.springcloud.producter.service;

import college.springcloud.producter.config.MyTransactionEvent;
import college.springcloud.producter.mapper.CfClearHeaderMapper;
import college.springcloud.producter.model.CfClearHeader;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author: xuxianbei
 * Date: 2020/9/21
 * Time: 16:39
 * Version:V1.0
 */
@Service
public class ClearHeaderService {

    @Resource
    CfClearHeaderMapper cfClearHeaderMapper;

    final
    ApplicationContext applicationContext;

    public ClearHeaderService(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Transactional
    public Integer transactionRollback() {
        applicationContext.publishEvent(new MyTransactionEvent("ddddd"));

        List<CfClearHeader> list =
                cfClearHeaderMapper.selectList(Wrappers.lambdaQuery(CfClearHeader.class).in(CfClearHeader::getClearId, Arrays.asList(1, 2, 3)));
        return cfClearHeaderMapper.batchInsert(list);
    }
}

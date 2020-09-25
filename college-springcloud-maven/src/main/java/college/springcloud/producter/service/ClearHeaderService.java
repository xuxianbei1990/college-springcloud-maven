package college.springcloud.producter.service;

import college.springcloud.producter.config.MyTransactionEvent;
import college.springcloud.producter.mapper.CfClearHeaderMapper;
import college.springcloud.producter.model.CfClearHeader;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        LambdaQueryWrapper<CfClearHeader> lambdaQueryWrapper =
                Wrappers.lambdaQuery(CfClearHeader.class);
//        lambdaQueryWrapper.last(grateThen(CfClearHeader::getBankAmount, CfClearHeader::getExchangeRate));
        List<CfClearHeader> list =
                cfClearHeaderMapper.selectList(lambdaQueryWrapper
                        .in(CfClearHeader::getClearId, Arrays.asList(1, 2, 3)));
        return cfClearHeaderMapper.batchInsert(list);
    }

    private <T> String grateThen(SFunction<T, ?> column, SFunction<T, ?> column2) {
        SerializedLambda lambda = LambdaUtils.resolve(column);
        SerializedLambda lambda2 = LambdaUtils.resolve(column2);
        String fieldName = getColumn(lambda, true);
        String fieldName2 = getColumn(lambda2, true);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(SqlKeyword.AND.getSqlSegment()).append(" ");
        stringBuilder.append(fieldName).append(SqlKeyword.GT.getSqlSegment()).append(fieldName2);
        return stringBuilder.toString();
    }

    private String getColumn(SerializedLambda lambda, boolean onlyColumn) throws MybatisPlusException {
        String fieldName = PropertyNamer.methodToProperty(lambda.getImplMethodName());
        Class<?> aClass = lambda.getInstantiatedType();

        Map<String, ColumnCache> columnMap = LambdaUtils.getColumnMap(aClass);

        ColumnCache columnCache = columnMap.get(LambdaUtils.formatKey(fieldName));
        Assert.notNull(columnCache, "can not find lambda cache for this property [%s] of entity [%s]",
                fieldName, aClass.getName());
        return onlyColumn ? columnCache.getColumn() : columnCache.getColumnSelect();
    }
}

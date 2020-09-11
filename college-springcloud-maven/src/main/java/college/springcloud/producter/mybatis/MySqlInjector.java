package college.springcloud.producter.mybatis;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;

import java.util.List;

/**
 * @author: xuxianbei
 * Date: 2020/9/10
 * Time: 14:58
 * Version:V1.0
 */
public class MySqlInjector extends DefaultSqlInjector {


    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
        List<AbstractMethod> list = super.getMethodList(mapperClass);
        list.add(new InsertBatch());
        list.add(new UpdateBatchById());
        return list;
    }
}

package college.springcloud.producter.mybatis;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author: xuxianbei
 * Date: 2020/9/10
 * Time: 14:38
 * Version:V1.0
 */
public interface BaseMapperEx<T> extends BaseMapper<T> {

    /**
     * 批量新增
     * 10w数据事务更新的速度5倍
     * @param list
     * @return
     */
    int batchInsert(List<T> list);


    /**
     * 批量更新
     * 速度是sql一条条拼装的10倍
     */
    int batchUpdateById(List<T> list);
}

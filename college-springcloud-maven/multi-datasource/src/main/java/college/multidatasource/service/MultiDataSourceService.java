package college.multidatasource.service;

import college.multidatasource.annotation.MyDS;
import college.multidatasource.dao.CfMultyImageMapper;
import college.multidatasource.dao.FCewebrityPlatFormKpiByDayMapper;
import college.multidatasource.model.CfMultyImage;
import college.multidatasource.model.FCewebrityPlatFormKpiByDay;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: xuxianbei
 * Date: 2021/5/19
 * Time: 11:22
 * Version:V1.0
 */
@Service
public class MultiDataSourceService {


    @Resource
    private CfMultyImageMapper cfMultyImageMapper;

    @Resource
    private FCewebrityPlatFormKpiByDayMapper fCewebrityPlatFormKpiByDayMapper;

    /**
     * 这里defaultdb和yml配置的数据源要一致,如果不一致，默认会使用主库，也就是master
     * @return
     */
    @DS("defaultdb")
    @MyDS("伊斯贝拉")
    public List<CfMultyImage> testDataSource() {
//        return testDataSource2();  证明了一个问题，这个还是受限AOP。 执行的是master
        return cfMultyImageMapper.selectList(Wrappers.lambdaQuery(CfMultyImage.class));
    }

    @DS("tocdb")
    public List<CfMultyImage> testDataSource2() {
        return cfMultyImageMapper.selectList(Wrappers.lambdaQuery(CfMultyImage.class));
    }

    @DS("click")
    public List<FCewebrityPlatFormKpiByDay> testDataSourceClick() {
        return fCewebrityPlatFormKpiByDayMapper.selectList(Wrappers.lambdaQuery(FCewebrityPlatFormKpiByDay.class));
    }


}

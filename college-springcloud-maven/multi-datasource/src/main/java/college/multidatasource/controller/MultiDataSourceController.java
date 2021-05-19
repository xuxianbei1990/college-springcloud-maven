package college.multidatasource.controller;

import college.multidatasource.model.CfMultyImage;
import college.multidatasource.service.MultiDataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: xuxianbei
 * Date: 2021/5/19
 * Time: 11:02
 * Version:V1.0
 */
@RestController
@RequestMapping("multi")
public class MultiDataSourceController {

    @Autowired
    private MultiDataSourceService multiDataSourceService;

    /**
     * 来源1
     * @return
     */
    @GetMapping("test/datasource/1")
    public List<CfMultyImage> testDataSource1() {
        return multiDataSourceService.testDataSource();
    }

    /**
     * 来源2
     * @return
     */
    @GetMapping("test/datasource/2")
    public List<CfMultyImage> testDataSource2() {
        return multiDataSourceService.testDataSource2();
    }
}

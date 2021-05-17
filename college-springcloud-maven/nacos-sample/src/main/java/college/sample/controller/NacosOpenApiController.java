package college.sample.controller;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: xuxianbei
 * Date: 2021/4/22
 * Time: 15:05
 * Version:V1.0
 */
@RequestMapping("/nacos")
@RestController
public class NacosOpenApiController {

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;


    @GetMapping("/open/api")
    public List<Instance> openApi() throws NacosException {
        NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();
        namingService.subscribe("chenfan-cloud-process", "DEV_GROUP", new EventListener() {
            @Override
            public void onEvent(Event event) {
                System.out.println("1");
            }
        });
        return namingService.getAllInstances("chenfan-cloud-mcn", "DEV_GROUP");
    }

}

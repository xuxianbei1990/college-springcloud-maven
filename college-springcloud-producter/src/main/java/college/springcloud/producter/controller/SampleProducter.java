package college.springcloud.producter.controller;

import college.springcloud.producter.model.SampleVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: xuxianbei
 * Date: 2020/8/31
 * Time: 21:11
 * Version:V1.0
 */
@RequestMapping("/test")
@RestController
public class SampleProducter {

    @GetMapping("sample")
    public SampleVo sample(){
        return new SampleVo();
    }
}

package college.rocketdleger.server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

/**
 *
 * @author: xuxianbei
 * Date: 2021/3/31
 * Time: 18:05
 * Version:V1.0
 */
@RestController
@RequestMapping("dledger")
public class MainDLedgerLog2Application {

    DLedgerLog dLedgerLog = new DLedgerLog("n2");

    @PostConstruct
    void init() {
        dLedgerLog.start();
    }

    /**
     * 添加句柄
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/append/handle/two")
    public String appendHandle() throws Exception {

        dLedgerLog.appendHandle();
        return "success";
    }

}

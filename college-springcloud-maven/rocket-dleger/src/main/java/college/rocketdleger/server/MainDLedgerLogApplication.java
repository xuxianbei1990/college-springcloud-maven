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
public class MainDLedgerLogApplication {

    DLedgerLog dLedgerLog = new DLedgerLog("n0");

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
    @GetMapping("/append/handle")
    public String appendHandle() throws Exception {

        dLedgerLog.appendHandle();
        return "success";
    }

}

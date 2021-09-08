package college.codegenerate.controller;

import college.codegenerate.module.CodeGenerateVO;
import college.codegenerate.module.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * @author: xuxianbei
 * Date: 2021/3/10
 * Time: 10:08
 * Version:V1.0
 */
@RestController
public class CodeGenerateController {

    @Autowired
    private RedisTemplate redisTemplate;

    private static Integer MAX_GENERATE_ID = 9999;

    private Object uniqueIdLock = new Object();

    /**
     * 线程安全 唯一id
     * 注意：1天只能产生9999个iD, 超过就会出现重复id, 不会报错， 支持多实例
     * 待优化
     * 无需优化。没那么高频率
     *
     * @param key
     * @return
     */
    public String generateThreadSafeUniqueId(String key) {
        //YPSH+年份后两位（如：21）+4位月日（如：0113）+4位自增序号（如：001）
        LocalDateTime localDateTime = LocalDateTime.now();
        String redisKey = key + localDateTime.format(DateTimeFormatter.ofPattern("YYMMdd"));
        RedisAtomicIntegerCustom redisAtomicInteger = getRedisAtomicInteger(redisKey);

        int i = redisAtomicInteger.decrementAndGet();
        if (i >= MAX_GENERATE_ID) {
            synchronized (uniqueIdLock) {
                if (redisAtomicInteger.get() >= MAX_GENERATE_ID) {
                    redisAtomicInteger.set(0, true);
                    i = 0;
                } else {
                    i = redisAtomicInteger.getAndAdd(1);
                }
            }
        }
        return String.format(redisKey + "%03d", i);
    }

    @GetMapping({"/code/generate"})
     R<CodeGenerateVO> generate(@RequestParam("receiptType") String receiptType) {
        CodeGenerateVO codeGenerateVO = new CodeGenerateVO();
        codeGenerateVO.setCode(generateThreadSafeUniqueId(receiptType));
        return R.ok(codeGenerateVO);
    }

    /**
     * 待优化
     * 无需优化。没那么高频率
     *
     * @param key
     * @return
     */
    private RedisAtomicIntegerCustom getRedisAtomicInteger(String key) {
        RedisAtomicIntegerCustom redisCount = new RedisAtomicIntegerCustom(key, redisTemplate.getConnectionFactory(), MAX_GENERATE_ID);
        redisCount.expire(31, TimeUnit.DAYS);
        return redisCount;
    }



}

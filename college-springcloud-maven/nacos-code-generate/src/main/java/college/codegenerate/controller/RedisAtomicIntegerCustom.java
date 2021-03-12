package college.codegenerate.controller;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * 定制RedisAtomic
 *
 * @author: xuxianbei
 * Date: 2021/1/29
 * Time: 10:18
 * Version:V1.0
 */
public class RedisAtomicIntegerCustom extends RedisAtomicInteger {

    private ValueOperations<String, Integer> operations;

    public RedisAtomicIntegerCustom(String redisCounter, RedisConnectionFactory factory, int initialValue) {
        super(redisCounter, factory, initialValue);
        initialization();
    }


    @Override
    public void set(int newValue) {
        initialization();
        operations.setIfAbsent(this.getKey(), newValue);
    }

    private void initialization() {
        if (Objects.isNull(operations)) {
            Field field = null;
            try {
                field = RedisAtomicInteger.class.getDeclaredField("operations");
                field.setAccessible(true);
                operations = (ValueOperations<String, Integer>) field.get(this);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void set(int newValue, boolean force) {
        initialization();
        if (force) {
            super.set(newValue);
        } else {
            operations.setIfAbsent(this.getKey(), newValue);
        }
    }

}

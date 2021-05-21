package college.multidatasource.annotation;

import java.lang.annotation.*;

/**
 * @author: xuxianbei
 * Date: 2021/5/20
 * Time: 16:44
 * Version:V1.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyDS {

    /**
     * groupName or specific database name or spring SPEL name.
     *
     * @return the database you want to switch
     */
    String value();
}

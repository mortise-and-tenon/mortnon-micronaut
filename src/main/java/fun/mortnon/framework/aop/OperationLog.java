package fun.mortnon.framework.aop;

import io.micronaut.aop.Around;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 操作日志注解
 *
 * @author dev2007
 * @date 2023/3/7
 */
@Documented
@Retention(RUNTIME)
@Target({METHOD})
@Around
public @interface OperationLog {
    /**
     * 操作名字
     *
     * @return
     */
    String value();
}

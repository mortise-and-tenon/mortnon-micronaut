package fun.mortnon.framework.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * @author dev2007
 * @date 2024/3/14
 */
@Slf4j
public class MortnonBeanUtils {
    public static void copy(Object orig, Object dest) {
        try {
            BeanUtils.copyProperties(dest, orig);
        } catch (Exception e) {
            log.warn("Copying object properties exception.");
        }
    }
}

package fun.mortnon.framework.properties;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import lombok.Data;

/**
 * @author dev2007
 * @date 2024/2/29
 */
@Data
@Context
@ConfigurationProperties(value = "mortnon.common")
public class CommonProperties {
    /**
     * 语言
     */
    private String lang;
}

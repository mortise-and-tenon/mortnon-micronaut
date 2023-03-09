package fun.mortnon.framework.i18n;

import io.micronaut.context.MessageSource;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.i18n.ResourceBundleMessageSource;
import jakarta.inject.Singleton;

/**
 * 国际化工具工厂
 *
 * @author dev2007
 * @date 2023/3/9
 */
@Factory
public class MessageSourceFactory {
    private static final String I18N = "mortnon.i18n.messages";

    /**
     * 生成 MessageSource 对象
     *
     * @return
     */
    @Singleton
    public MessageSource createMessageSource() {
        return new ResourceBundleMessageSource(I18N);
    }
}

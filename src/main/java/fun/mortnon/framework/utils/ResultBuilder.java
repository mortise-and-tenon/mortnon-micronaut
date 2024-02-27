package fun.mortnon.framework.utils;

import fun.mortnon.framework.enums.ErrorCodeEnum;
import fun.mortnon.framework.vo.MortnonResult;
import io.micronaut.context.MessageSource;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.Locale;

/**
 * @author dev2007
 * @date 2024/2/27
 */
@Singleton
public class ResultBuilder {
    @Inject
    private MessageSource messageSource;

    private String lang = "zh";

    public MortnonResult build(ErrorCodeEnum errorCodeEnum) {
        String message = messageSource.getMessage(errorCodeEnum.getI18n(), errorCodeEnum.getDescription(), new Locale(lang));
        return MortnonResult.fail(errorCodeEnum, message);
    }
}

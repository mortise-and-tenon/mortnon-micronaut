package fun.mortnon.framework.utils;

import fun.mortnon.framework.enums.ErrorCodeEnum;
import fun.mortnon.framework.properties.CommonProperties;
import fun.mortnon.framework.vo.MortnonResult;
import io.micronaut.context.MessageSource;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.Locale;
import java.util.Map;

/**
 * @author dev2007
 * @date 2024/2/27
 */
@Singleton
public class ResultBuilder {

    private MessageSource messageSource;

    private CommonProperties commonProperties;

    private String lang;

    @Inject
    public ResultBuilder(MessageSource messageSource, CommonProperties commonProperties) {
        this.messageSource = messageSource;
        this.commonProperties = commonProperties;
        this.lang = commonProperties.getLang();
    }

    public MortnonResult build(ErrorCodeEnum errorCodeEnum) {
        String message = messageSource.getMessage(errorCodeEnum.getI18n(), errorCodeEnum.getDescription(), new Locale(lang));
        return MortnonResult.fail(errorCodeEnum, message);
    }

    public MortnonResult buildWithParameters(ErrorCodeEnum errorCodeEnum, Map<String, Object> parameters) {
        String message = messageSource.getMessage(errorCodeEnum.getI18n(), errorCodeEnum.getDescription(), new Locale(lang), parameters);
        return MortnonResult.fail(errorCodeEnum, message);
    }

    public MortnonResult buildWithData(ErrorCodeEnum errorCodeEnum, Object attachData) {
        String message = messageSource.getMessage(errorCodeEnum.getI18n(), errorCodeEnum.getDescription(), new Locale(lang));
        return MortnonResult.fail(errorCodeEnum, message, attachData);
    }

    public MortnonResult build(ErrorCodeEnum errorCodeEnum, String i18nMessage) {
        String message = messageSource.getMessage(i18nMessage, i18nMessage, new Locale(lang));
        return MortnonResult.fail(errorCodeEnum, message);
    }
}

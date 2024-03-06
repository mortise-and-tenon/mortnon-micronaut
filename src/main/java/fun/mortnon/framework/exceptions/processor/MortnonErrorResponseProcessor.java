package fun.mortnon.framework.exceptions.processor;

import fun.mortnon.framework.enums.ErrorCodeEnum;
import fun.mortnon.framework.vo.MortnonResult;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.server.exceptions.response.ErrorContext;
import io.micronaut.http.server.exceptions.response.ErrorResponseProcessor;
import io.micronaut.http.server.exceptions.response.HateoasErrorResponseProcessor;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据校验异常，响应处理器
 *
 * @author dev2007
 * @date 2023/3/13
 */
@Singleton
@Replaces(HateoasErrorResponseProcessor.class)
@Slf4j
public class MortnonErrorResponseProcessor implements ErrorResponseProcessor<MortnonResult> {
    @NonNull
    @Override
    public MutableHttpResponse<MortnonResult> processResponse(@NonNull ErrorContext errorContext, @NonNull MutableHttpResponse<?> response) {
        MortnonResult failResult = MortnonResult.fail(ErrorCodeEnum.PARAM_ERROR, "");

        String errorMessage = "";
        if (errorContext.hasErrors()) {
            errorMessage = errorContext.getErrors().get(0).getMessage();
            failResult.setMessage(errorContext.getErrors().get(0).getMessage());
        }

        log.warn("Obtain unhandled exceptions:Request:{},Error:{}.", errorContext.getRequest(), errorMessage);

        return response.status(HttpStatus.BAD_REQUEST).body(failResult).contentType(MediaType.APPLICATION_JSON_TYPE);
    }
}

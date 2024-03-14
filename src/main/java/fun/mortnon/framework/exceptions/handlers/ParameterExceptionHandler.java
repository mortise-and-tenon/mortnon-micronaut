package fun.mortnon.framework.exceptions.handlers;

import fun.mortnon.framework.exceptions.ParameterException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

/**
 * 传递参数异常
 *
 * @author dev2007
 * @date 2023/2/21
 */
@Produces
@Singleton
@Requires(classes = {ParameterException.class, ExceptionHandler.class})
public class ParameterExceptionHandler extends BaseExceptionHandler<ParameterException> {
    @Override
    HttpResponse handle(ParameterException exception) {
        return HttpResponse.badRequest(resultBuilder.build(exception.getErrorCodeEnum()));
    }
}

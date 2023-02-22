package fun.mortnon.framework.exceptions.handlers;

import fun.mortnon.framework.exceptions.ParameterException;
import fun.mortnon.framework.vo.MortnonResult;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

/**
 * @author dev2007
 * @date 2023/2/21
 */
@Produces
@Singleton
@Requires(classes = {ParameterException.class, ExceptionHandler.class})
public class ParameterExceptionHandler implements ExceptionHandler<ParameterException, HttpResponse> {
    public HttpResponse handle(HttpRequest request, ParameterException exception) {
        return HttpResponse.notFound(MortnonResult.fail(exception.getErrorCodeEnum(), exception.getMessage()));
    }
}

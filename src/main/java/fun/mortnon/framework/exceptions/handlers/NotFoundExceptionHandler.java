package fun.mortnon.framework.exceptions.handlers;

import fun.mortnon.framework.exceptions.NotFoundException;
import fun.mortnon.framework.utils.ResultBuilder;
import fun.mortnon.framework.vo.MortnonResult;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * @author dev2007
 * @date 2023/2/21
 */
@Produces
@Singleton
@Requires(classes = {NotFoundException.class, ExceptionHandler.class})
public class NotFoundExceptionHandler implements ExceptionHandler<NotFoundException, HttpResponse> {
    @Inject
    private ResultBuilder resultBuilder;

    @Override
    public HttpResponse handle(HttpRequest request, NotFoundException exception) {
        return HttpResponse.notFound(resultBuilder.build(exception.getErrorCodeEnum(), exception.getMessage()));
    }
}

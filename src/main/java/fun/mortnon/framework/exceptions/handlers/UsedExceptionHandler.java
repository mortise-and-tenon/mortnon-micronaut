package fun.mortnon.framework.exceptions.handlers;

import fun.mortnon.framework.exceptions.NotFoundException;
import fun.mortnon.framework.exceptions.UsedException;
import fun.mortnon.framework.vo.MortnonResult;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

/**
 * @author dev2007
 * @date 2023/2/24
 */
@Produces
@Singleton
@Requires(classes = {UsedException.class, ExceptionHandler.class})
public class UsedExceptionHandler implements ExceptionHandler<UsedException, HttpResponse> {

    @Override
    public HttpResponse handle(HttpRequest request, UsedException exception) {
        return HttpResponse.badRequest(MortnonResult.fail(exception.getErrorCodeEnum(), exception.getMessage()));
    }
}

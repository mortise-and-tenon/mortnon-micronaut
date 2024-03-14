package fun.mortnon.framework.exceptions.handlers;

import fun.mortnon.framework.exceptions.NotFoundException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

/**
 * 数据未找到异常
 *
 * @author dev2007
 * @date 2023/2/21
 */
@Produces
@Singleton
@Requires(classes = {NotFoundException.class, ExceptionHandler.class})
public class NotFoundExceptionHandler extends BaseExceptionHandler<NotFoundException> {
    @Override
    HttpResponse handle(NotFoundException exception) {
        return HttpResponse.notFound(resultBuilder.build(exception.getErrorCodeEnum()));
    }
}

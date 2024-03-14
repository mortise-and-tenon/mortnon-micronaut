package fun.mortnon.framework.exceptions.handlers;

import fun.mortnon.framework.exceptions.UsedException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

/**
 * 数据已被使用异常
 *
 * @author dev2007
 * @date 2023/2/24
 */
@Produces
@Singleton
@Requires(classes = {UsedException.class, ExceptionHandler.class})
public class UsedExceptionHandler extends BaseExceptionHandler<UsedException> {
    @Override
    HttpResponse handle(UsedException exception) {
        return HttpResponse.badRequest(resultBuilder.build(exception.getErrorCodeEnum()));
    }
}

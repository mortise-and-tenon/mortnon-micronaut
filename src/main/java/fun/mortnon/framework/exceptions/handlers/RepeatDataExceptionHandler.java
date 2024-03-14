package fun.mortnon.framework.exceptions.handlers;

import fun.mortnon.framework.exceptions.RepeatDataException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

/**
 * 数据重复异常
 *
 * @author dev2007
 * @date 2023/2/21
 */
@Produces
@Singleton
@Requires(classes = {RepeatDataException.class, ExceptionHandler.class})
public class RepeatDataExceptionHandler extends BaseExceptionHandler<RepeatDataException> {

    @Override
    HttpResponse handle(RepeatDataException exception) {
        return HttpResponse.badRequest(resultBuilder.buildWithData(exception.getErrorCodeEnum(), exception.getAttachData()));
    }
}

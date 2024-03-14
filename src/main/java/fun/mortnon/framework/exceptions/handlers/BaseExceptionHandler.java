package fun.mortnon.framework.exceptions.handlers;

import fun.mortnon.framework.utils.ResultBuilder;
import fun.mortnon.service.log.SysLogService;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import jakarta.inject.Inject;
import io.micronaut.http.server.exceptions.ExceptionHandler;

/**
 * 异常处理基类
 *
 * @author dev2007
 * @date 2024/3/14
 */
public abstract class BaseExceptionHandler<T extends Throwable> implements ExceptionHandler<T, HttpResponse> {
    @Inject
    protected ResultBuilder resultBuilder;

    @Inject
    private SysLogService sysLogService;

    @Override
    public HttpResponse handle(HttpRequest request, T exception) {
        HttpResponse response = handle(exception);
        sysLogService.buildLog(request, response);
        return response;
    }

    /**
     * 子类生成异常下的 HttpResponse
     *
     * @return
     */
    abstract HttpResponse handle(T exception);
}

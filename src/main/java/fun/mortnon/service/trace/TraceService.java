package fun.mortnon.service.trace;

import fun.mortnon.framework.utils.IpUtil;
import fun.mortnon.web.filter.model.TraceLog;
import io.micronaut.context.annotation.Prototype;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.runtime.http.scope.RequestScope;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;

/**
 * Trace Request 服务
 *
 * @author dev2007
 * @date 2023/2/6
 */
@RequestScope
@Slf4j
public class TraceService {
    private TraceLog traceLog;

    /**
     * 记录请求
     *
     * @param request
     * @return
     */
    public Publisher<Boolean> traceBefore(HttpRequest<?> request) {
        Callable<Boolean> callable = () -> {
            traceLog = new TraceLog(IpUtil.getRequestIp(request), request.getMethod(), request.getUri());
            return true;
        };

        return Mono.fromCallable(callable).subscribeOn(Schedulers.boundedElastic()).flux();
    }

    public MutableHttpResponse traceAfter(MutableHttpResponse<?> response) {
        traceLog.setStatus(response.getStatus());
        traceLog.print();
        return response;
    }
}

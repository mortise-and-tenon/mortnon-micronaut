package fun.mortnon.service.trace;

import fun.mortnon.framework.utils.IpUtil;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpRequest;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.time.Instant;
import java.util.concurrent.Callable;

/**
 * Trace Request 服务
 *
 * @author dev2007
 * @date 2023/2/6
 */
@Singleton
@Slf4j
public class TraceService {
    private static final String TRACE_FORMAT = "[{};{}][{} - {}]";

    /**
     * 记录请求
     *
     * @param request
     * @return
     */
    public Publisher<Boolean> trace(HttpRequest<?> request) {
        Callable<Boolean> callable = () -> {
            HttpMethod method = request.getMethod();
            String ip = IpUtil.getRequestIp(request);
            URI uri = request.getUri();
            log.info(TRACE_FORMAT, Instant.now(), ip, method, uri);
            return true;
        };

        return Mono.fromCallable(callable).subscribeOn(Schedulers.boundedElastic()).flux();
    }
}

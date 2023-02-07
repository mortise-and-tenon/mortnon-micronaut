package fun.mortnon.service.trace;

import io.micronaut.http.HttpRequest;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Trace Request 服务
 *
 * @author dev2007
 * @date 2023/2/6
 */
@Singleton
@Slf4j
public class TraceService {
    /**
     * 记录请求
     *
     * @param request
     * @return
     */
    public Publisher<Boolean> trace(HttpRequest<?> request) {
        return Mono.fromCallable(() -> {
                    log.info("Request:{}", request.getUri());
                    return true;
                }).subscribeOn(Schedulers.boundedElastic())
                .flux();
    }
}

package fun.mortnon.web.filter;

import fun.mortnon.service.trace.TraceService;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

/**
 * 记录所有访问情况
 *
 * @author dev2007
 * @date 2023/2/6
 */

@Filter("/**")
@Slf4j
public class TraceFilter implements HttpServerFilter {
    private final TraceService traceService;

    public TraceFilter(TraceService traceService) {
        this.traceService = traceService;
    }

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
        return Flux.from(traceService.traceBefore(request))
                .switchMap(result -> chain.proceed(request))
                .map(response -> traceService.traceAfter(response));
    }
}

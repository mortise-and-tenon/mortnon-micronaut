package fun.mortnon.micronaut.pac4j.filter;

import fun.mortnon.micronaut.pac4j.context.session.MicronautSessionStoreFactory;
import io.micronaut.core.util.AntPathMatcher;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.core.util.PathMatcher;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.micronaut.session.Session;
import io.micronaut.session.SessionStore;
import io.micronaut.session.http.HttpSessionIdResolver;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.config.Config;
import org.pac4j.core.util.CommonHelper;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

/**
 * @author dev2007
 * @date 2023/3/1
 */
@Slf4j
public abstract class AbstractConfigFilter implements HttpServerFilter {
    private Config config;

    private SessionStore<Session> sessionStore;

    protected MicronautSessionStoreFactory micronautSessionStoreFactory;

    protected HttpSessionIdResolver[] resolvers;

    protected final AntPathMatcher pathMatcher = PathMatcher.ANT;

    protected void setResolvers(HttpSessionIdResolver[] resolvers) {
        this.resolvers = resolvers;
    }

    protected void setSessionStore(SessionStore<Session> sessionStore) {
        this.sessionStore = sessionStore;
    }

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
        initSession(request);
        return internalFilter(request, chain);
    }

    private void initSession(HttpRequest<?> request) {
        Session session = null;

        for (HttpSessionIdResolver resolver : resolvers) {
            List<String> ids = resolver.resolveIds(request);
            if (CollectionUtils.isNotEmpty(ids)) {
                String id = ids.get(0);
                Mono<Optional<Session>> sessionLookup = Mono.fromCompletionStage(sessionStore.findSession(id));
                session = sessionLookup.block().orElse(null);
                if (null != session) {
                    break;
                }
            }
        }

        micronautSessionStoreFactory = new MicronautSessionStoreFactory(sessionStore, session);
    }

    /**
     * 子类具体实现
     *
     * @param request
     * @param chain
     * @return
     */
    protected abstract Publisher<MutableHttpResponse<?>> internalFilter(HttpRequest<?> request, ServerFilterChain chain);

    public Config getSharedConfig() {
        if (this.config == null) {
            return Config.INSTANCE;
        }
        return this.config;
    }

    public void setSharedConfig(final Config config) {
        CommonHelper.assertNotNull("config", config);
        this.config = config;
        Config.setConfig(config);
    }

    public void setConfig(Config config) {
        CommonHelper.assertNotNull("config", config);
        this.config = config;
    }
}

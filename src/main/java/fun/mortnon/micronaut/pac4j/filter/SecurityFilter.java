package fun.mortnon.micronaut.pac4j.filter;

import fun.mortnon.micronaut.pac4j.configuration.Pac4jConfigurationProperties;
import fun.mortnon.micronaut.pac4j.context.MicronautWebContextFactory;
import fun.mortnon.micronaut.pac4j.http.adapter.MicronautHttpActionAdapter;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponseFactory;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.ServerFilterChain;
import io.micronaut.session.Session;
import io.micronaut.session.http.HttpSessionIdResolver;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.engine.SecurityLogic;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.util.FindBest;
import org.pac4j.core.util.security.SecurityEndpoint;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import static io.micronaut.http.filter.ServerFilterPhase.SESSION;

/**
 * @author dev2007
 * @date 2023/3/1
 */
@Filter("/**")
@Slf4j
@Requires(beans = org.pac4j.core.config.Config.class)
public class SecurityFilter extends AbstractConfigFilter implements SecurityEndpoint {
    private SecurityLogic securityLogic;

    private String clients;

    private String authorizers;

    private String matchers;

    private HttpActionAdapter httpActionAdapter;

    @Inject
    private Pac4jConfigurationProperties pac4jConfigurationProperties;

    public SecurityFilter() {
    }

    @Inject
    public SecurityFilter(Config config, io.micronaut.session.SessionStore<Session> sessionStore, HttpSessionIdResolver[] resolvers) {
        setSharedConfig(config);
        setSessionStore(sessionStore);
        setResolvers(resolvers);
    }

    @Override
    public int getOrder() {
        return SESSION.after();
    }

    public String getClients() {
        return clients;
    }

    @Override
    public void setClients(String clients) {
        this.clients = clients;
    }

    public String getAuthorizers() {
        return authorizers;
    }

    @Override
    public void setAuthorizers(String authorizers) {
        this.authorizers = authorizers;
    }

    public String getMatchers() {
        return matchers;
    }

    @Override
    public void setMatchers(String matchers) {
        this.matchers = matchers;
    }

    public SecurityLogic getSecurityLogic() {
        return securityLogic;
    }

    @Override
    public void setSecurityLogic(SecurityLogic securityLogic) {
        this.securityLogic = securityLogic;
    }

    public HttpActionAdapter getHttpActionAdapter() {
        return httpActionAdapter;
    }

    @Override
    public void setHttpActionAdapter(HttpActionAdapter httpActionAdapter) {
        this.httpActionAdapter = httpActionAdapter;
    }

    @Override
    protected Publisher<MutableHttpResponse<?>> internalFilter(HttpRequest<?> request, ServerFilterChain chain) {
        String path = request.getUri().getPath();
        //如果是回调地址，不做处理
        if (pathMatcher.matches(pac4jConfigurationProperties.getCallbackUri(), path)) {
            return chain.proceed(request);
        }

        //如果非全局 uri 进入认证，且 uri 不匹配，不做处理
        if (!pac4jConfigurationProperties.isGlobal() && !pathMatcher.matches(pac4jConfigurationProperties.getLoginUri(), path)) {
            return chain.proceed(request);
        }

        final Config config = getSharedConfig();

        final HttpActionAdapter bestAdapter = FindBest.httpActionAdapter(httpActionAdapter, config, MicronautHttpActionAdapter.INSTANCE);
        final SecurityLogic bestLogic = FindBest.securityLogic(securityLogic, config, DefaultSecurityLogic.INSTANCE);

        final WebContext context = FindBest.webContextFactory(null, config, MicronautWebContextFactory.INSTANCE).newContext(request, HttpResponseFactory.INSTANCE.ok());
        final SessionStore sessionStore = FindBest.sessionStoreFactory(null, config, micronautSessionStoreFactory).newSessionStore(request);

        return Mono.just((MutableHttpResponse<?>) bestLogic.perform(context, sessionStore, config,
                (ctx, session, profiles, parameters) -> chain.proceed(request),
                bestAdapter, clients, authorizers, matchers));
    }


}

package fun.mortnon.micronaut.pac4j.filter;

import fun.mortnon.micronaut.pac4j.configuration.Pac4jConfigurationProperties;
import fun.mortnon.micronaut.pac4j.context.MicronautWebContextFactory;
import fun.mortnon.micronaut.pac4j.http.adapter.MicronautHttpActionAdapter;
import fun.mortnon.micronaut.pac4j.logic.MicronautCallbackLogic;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
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
import org.pac4j.core.engine.CallbackLogic;
import org.pac4j.core.engine.DefaultCallbackLogic;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.util.FindBest;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import static io.micronaut.http.filter.ServerFilterPhase.SESSION;

/**
 * @author dev2007
 * @date 2023/3/2
 */
@Filter("/**")
@Slf4j
@Requires(beans = org.pac4j.core.config.Config.class)
public class CallbackFilter extends AbstractConfigFilter {

    private CallbackLogic callbackLogic;

    private String defaultUrl;

    private Boolean renewSession;

    private String defaultClient;

    @Inject
    private Pac4jConfigurationProperties pac4jConfigurationProperties;

    @Inject
    private MicronautCallbackLogic micronautCallbackLogic;

    public CallbackFilter() {
    }

    @Inject
    public CallbackFilter(Config config, io.micronaut.session.SessionStore<Session> sessionStore, HttpSessionIdResolver[] resolvers) {
        setSharedConfig(config);
        setSessionStore(sessionStore);
        setResolvers(resolvers);
    }

    @Override
    public int getOrder() {
        return SESSION.after();
    }

    @Override
    protected Publisher<MutableHttpResponse<?>> internalFilter(HttpRequest<?> request, ServerFilterChain chain) {
        String path = request.getUri().getPath();
        //如果不是回调地址，不做处理
        if (!pathMatcher.matches(pac4jConfigurationProperties.getCallbackUri(), path)) {
            return chain.proceed(request);
        }

        final Config config = getSharedConfig();

        final HttpActionAdapter bestAdapter = FindBest.httpActionAdapter(null, config, MicronautHttpActionAdapter.INSTANCE);
        final CallbackLogic bestLogic = FindBest.callbackLogic(callbackLogic, config, micronautCallbackLogic);

        final WebContext context = FindBest.webContextFactory(null, config, MicronautWebContextFactory.INSTANCE).newContext(request, HttpResponse.ok());
        final SessionStore sessionStore = FindBest.sessionStoreFactory(null, config, micronautSessionStoreFactory).newSessionStore(request, HttpResponse.ok());


        return Mono.just((MutableHttpResponse<?>) bestLogic.perform(context, sessionStore, config, bestAdapter,
                this.defaultUrl, this.renewSession, this.defaultClient));
    }

    public String getDefaultUrl() {
        return this.defaultUrl;
    }

    public void setDefaultUrl(final String defaultUrl) {
        this.defaultUrl = defaultUrl;
    }

    public Boolean getRenewSession() {
        return renewSession;
    }

    public void setRenewSession(final Boolean renewSession) {
        this.renewSession = renewSession;
    }

    public CallbackLogic getCallbackLogic() {
        return callbackLogic;
    }

    public void setCallbackLogic(final CallbackLogic callbackLogic) {
        this.callbackLogic = callbackLogic;
    }

    public String getDefaultClient() {
        return defaultClient;
    }

    public void setDefaultClient(final String defaultClient) {
        this.defaultClient = defaultClient;
    }
}

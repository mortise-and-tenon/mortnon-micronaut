package fun.mortnon.micronaut.pac4j.logic;

import fun.mortnon.micronaut.pac4j.context.MicronautWebContext;
import fun.mortnon.micronaut.pac4j.security.AuthenticationGenerator;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.event.LoginSuccessfulEvent;
import io.micronaut.security.handlers.RedirectingLoginHandler;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.engine.DefaultCallbackLogic;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.profile.UserProfile;

/**
 * @author dev2007
 * @date 2023/3/2
 */
@Singleton
@Slf4j
public class MicronautCallbackLogic extends DefaultCallbackLogic {

    @Inject
    private RedirectingLoginHandler loginHandler;

    @Inject
    private ApplicationEventPublisher eventPublisher;

    @Inject
    private AuthenticationGenerator authenticationGenerator;

    private Authentication authentication;

    @Override
    public Object perform(final WebContext webContext, final SessionStore sessionStore, final Config config,
                          final HttpActionAdapter httpActionAdapter, final String inputDefaultUrl, final Boolean inputRenewSession,
                          final String defaultClient) {

        Object defaultPerform = super.perform(webContext, sessionStore, config, httpActionAdapter, inputDefaultUrl, inputRenewSession, defaultClient);

        if (null != authentication) {
            log.trace("Authentication succeeded. User [{}] is now logged in", authentication.getName());
            eventPublisher.publishEvent(new LoginSuccessfulEvent(authentication));
            return loginHandler.loginSuccess(authentication, ((MicronautWebContext)webContext).getNativeRequest());
        } else {
            return defaultPerform;
        }
    }

    @Override
    protected void saveUserProfile(final WebContext context, final SessionStore sessionStore, final Config config,
                                   final UserProfile profile, final boolean saveProfileInSession, final boolean multiProfile,
                                   final boolean renewSession) {
        final var manager = getProfileManager(context, sessionStore);
        if (profile != null) {
            manager.save(saveProfileInSession, profile, multiProfile);
            if (renewSession) {
                renewSession(context, sessionStore, config);
            }

            authentication = authenticationGenerator.create(profile);
        }
    }
}

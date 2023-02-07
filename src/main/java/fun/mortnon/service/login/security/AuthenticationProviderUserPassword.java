package fun.mortnon.service.login.security;

import fun.mortnon.framework.constants.LoginTypeConstants;
import fun.mortnon.service.login.LoginService;
import fun.mortnon.service.login.enums.LoginType;
import fun.mortnon.service.login.model.LoginUser;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

/**
 * @author dev2007
 * @date 2023/2/7
 */
@Singleton
public class AuthenticationProviderUserPassword implements AuthenticationProvider {
    private LoginService loginService;

    public AuthenticationProviderUserPassword(@Named(LoginTypeConstants.PASSWORD) LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public Publisher<AuthenticationResponse> authenticate(@Nullable HttpRequest<?> httpRequest,
                                                          AuthenticationRequest<?, ?> authenticationRequest) {
        return Flux.create(emitter -> {
            boolean authResult = loginService.authenticate(authenticationRequest);
            if (authResult) {
                emitter.next(AuthenticationResponse.success((String) authenticationRequest.getIdentity()));
                emitter.complete();
            } else {
                emitter.error(AuthenticationResponse.exception(AuthenticationFailureReason.CUSTOM));
            }
        }, FluxSink.OverflowStrategy.ERROR);
    }
}
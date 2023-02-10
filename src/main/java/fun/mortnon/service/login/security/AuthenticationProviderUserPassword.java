package fun.mortnon.service.login.security;

import fun.mortnon.dal.sys.entity.SysRole;
import fun.mortnon.framework.constants.LoginTypeConstants;
import fun.mortnon.service.login.LoginService;
import fun.mortnon.service.login.enums.LoginType;
import fun.mortnon.service.login.model.LoginUser;
import fun.mortnon.service.sys.SysUserService;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author dev2007
 * @date 2023/2/7
 */
@Singleton
public class AuthenticationProviderUserPassword implements AuthenticationProvider {
    private LoginService loginService;

    @Inject
    private SysUserService sysUserService;

    public AuthenticationProviderUserPassword(@Named(LoginTypeConstants.PASSWORD) LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public Publisher<AuthenticationResponse> authenticate(@Nullable HttpRequest<?> httpRequest,
                                                          AuthenticationRequest<?, ?> authenticationRequest) {
        return Mono.<AuthenticationResponse>create(emitter -> {
            SysRole role = loginService.authenticate(authenticationRequest)
                    .flatMap(result -> {
                        if (result) {
                            return sysUserService.queryUserRole((String) authenticationRequest.getIdentity());
                        }
                        return null;
                    }).block();
            if (null == role) {
                emitter.error(AuthenticationResponse.exception(AuthenticationFailureReason.CUSTOM));
            }
            List<String> roleList = new ArrayList<>();
            roleList.add(role.getIdentifier());
            emitter.success(AuthenticationResponse.success((String) authenticationRequest.getIdentity(), roleList));
        });
    }
}
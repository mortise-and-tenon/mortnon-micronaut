package fun.mortnon.service.login.security;

import fun.mortnon.dal.sys.entity.SysProject;
import fun.mortnon.dal.sys.entity.SysRole;
import fun.mortnon.framework.constants.LoginTypeConstants;
import fun.mortnon.framework.constants.login.ClaimsProject;
import fun.mortnon.service.login.CaptchaService;
import fun.mortnon.service.login.LoginService;
import fun.mortnon.service.login.enums.LoginType;
import fun.mortnon.service.login.model.LoginUser;
import fun.mortnon.service.sys.SysUserService;
import fun.mortnon.web.vo.login.PasswordLoginCredentials;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

import static fun.mortnon.framework.constants.login.ClaimsConstants.PROJECT;

/**
 * @author dev2007
 * @date 2023/2/7
 */
@Singleton
@Slf4j
public class AuthenticationProviderUserPassword implements AuthenticationProvider {
    private LoginService loginService;

    /**
     * 验证码服务
     */
    @Inject
    private CaptchaService captchaService;

    @Inject
    private SysUserService sysUserService;

    public AuthenticationProviderUserPassword(@Named(LoginTypeConstants.PASSWORD) LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public Publisher<AuthenticationResponse> authenticate(@Nullable HttpRequest<?> httpRequest,
                                                          AuthenticationRequest<?, ?> authenticationRequest) {
        return Mono.<AuthenticationResponse>create(emitter -> {
            List<SysRole> roleList = verifyCaptcha(authenticationRequest)
                    .flatMap(verify -> {
                        if (!verify) {
                            log.info("Verification code failed.");
                            return Mono.error(AuthenticationResponse.exception(AuthenticationFailureReason.CUSTOM));
                        }

                        return loginService.authenticate(authenticationRequest)
                                .flatMap(result -> {
                                    if (result) {
                                        return sysUserService.queryUserRole((String) authenticationRequest.getIdentity()).collectList();
                                    }
                                    //认证失败
                                    return Mono.error(AuthenticationResponse.exception(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH));
                                });
                    }).block();

            if (CollectionUtils.isEmpty(roleList)) {
                emitter.error(AuthenticationResponse.exception(AuthenticationFailureReason.UNKNOWN));
            }

            Set<String> roleIdentifierSet = Optional.ofNullable(roleList)
                    .map(list -> list.stream().map(SysRole::getIdentifier)
                            .collect(Collectors.toSet())).orElse(new HashSet<>());

            Map<String, Object> attributes = new HashMap<>();


            Set<ClaimsProject> projectSet = sysUserService.queryUserProject((String) authenticationRequest.getIdentity())
                    .map(project -> new ClaimsProject(project.getId(), project.getName()))
                    .collect(Collectors.toSet()).block();

            if (CollectionUtils.isNotEmpty(projectSet)) {
                attributes.put(PROJECT, projectSet);
            }

            emitter.success(AuthenticationResponse.success((String) authenticationRequest.getIdentity(), roleIdentifierSet, attributes));
        });
    }

    /**
     * 校验验证码
     *
     * @param authenticationRequest
     * @return
     */
    private Mono<Boolean> verifyCaptcha(AuthenticationRequest<?, ?> authenticationRequest) {
        return captchaService.isEnabled()
                .flatMap(result -> {
                    //未启用验证码，直接忽略验证码校验
                    if (!result) {
                        return Mono.just(true);
                    }
                    if (authenticationRequest instanceof PasswordLoginCredentials) {
                        PasswordLoginCredentials passwordLoginCredentials = (PasswordLoginCredentials) authenticationRequest;
                        return captchaService.verifyCaptcha(passwordLoginCredentials.getVerifyKey(), passwordLoginCredentials.getVerifyCode());
                    }

                    return Mono.just(true);
                });

    }
}
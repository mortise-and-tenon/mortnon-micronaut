package fun.mortnon.service.login.impl;

import fun.mortnon.dal.sys.entity.SysUser;
import fun.mortnon.framework.constants.LoginTypeConstants;
import fun.mortnon.service.login.LoginService;
import fun.mortnon.service.sys.SysUserService;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author dongfangzan
 * @date 27.4.21 4:18 下午
 */
@Singleton
@Slf4j
@Named(LoginTypeConstants.PASSWORD)
public class PasswordLoginServiceImpl implements LoginService {

    @Inject
    private SysUserService sysUserService;

    @Override
    public Mono<Boolean> authenticate(AuthenticationRequest<?, ?> authenticationRequest) {
        String userName = (String) authenticationRequest.getIdentity();
        String password = (String) authenticationRequest.getSecret();

        return sysUserService.getUserByUsername(userName)
                .flatMap(sysUser -> {
                    if (null == sysUser) {
                        log.info("User {} does not exist.", userName);
                        return Mono.error(AuthenticationResponse.exception(AuthenticationFailureReason.USER_NOT_FOUND));
                    }

                    if (!sysUser.isStatus()) {
                        log.info("User {} deactivated.", userName);
                        return Mono.error(AuthenticationResponse.exception(AuthenticationFailureReason.USER_DISABLED));
                    }

                    boolean auth = auth(sysUser, password);
                    if (!auth) {
                        log.info("User {} password does not match.", userName);
                        return Mono.error(AuthenticationResponse.exception(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH));
                    }

                    return Mono.just(true);
                });
    }

    private boolean auth(SysUser sysUser, String password) {
        password = DigestUtils.sha256Hex(password + sysUser.getSalt());
        return sysUser.getPassword().equals(password);
    }
}

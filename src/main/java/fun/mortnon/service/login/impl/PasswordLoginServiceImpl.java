package fun.mortnon.service.login.impl;

import fun.mortnon.dal.sys.entity.SysUser;
import fun.mortnon.framework.constants.LoginTypeConstants;
import fun.mortnon.framework.properties.CommonProperties;
import fun.mortnon.framework.utils.IpUtil;
import fun.mortnon.service.login.LoginFactory;
import fun.mortnon.service.login.LoginService;
import fun.mortnon.service.login.LoginStorageService;
import fun.mortnon.service.sys.ConfigService;
import fun.mortnon.service.sys.EncryptService;
import fun.mortnon.service.sys.SysUserService;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import reactor.core.publisher.Mono;

import java.time.Duration;

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

    @Inject
    private ConfigService configService;

    @Inject
    private EncryptService encryptService;

    /**
     * 登录工厂
     */
    @Inject
    private LoginFactory loginFactory;

    @Inject
    private CommonProperties commonProperties;

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

                    return auth(sysUser, password);
                })
                .flatMap(auth -> {
                    if (!auth) {
                        log.info("User {} password does not match.", userName);
                        return Mono.error(AuthenticationResponse.exception(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH));
                    }

                    return Mono.just(true);
                });
    }

    private Mono<Boolean> auth(SysUser sysUser, String password) {
        return configService.queryConfig().map(config -> {
                    if (config.isPasswordEncrypt()) {
                        return encryptService.decryptByCacheKey(password);
                    }
                    return password;
                })
                .map(decryptPassword -> {
                    decryptPassword = DigestUtils.sha256Hex(decryptPassword + sysUser.getSalt());
                    return sysUser.getPassword().equals(decryptPassword);
                });

    }

    @Override
    public long interceptLogin(HttpRequest<?> request) {
        String ip = IpUtil.getRequestIp(request);
        long lockTime = getStorageService().isLockLoginTimeExist(lockKey(ip));
        //已经处于锁定状态
        if (lockTime > 0) {
            return lockTime;
        }

        return configService.queryConfig().map(config -> {
            int newCount = getStorageService().saveLock(lockCountKey(ip), commonProperties.getCheckDuration());
            if (newCount + 1 >= config.getTryCount()) {
                getStorageService().lockLogin(lockKey(ip), config.getLockTime());
                return config.getLockTime();
            }
            return 0;
        }).block(Duration.ofSeconds(5));
    }


    private LoginStorageService getStorageService() {
        return loginFactory.getConfigLoginStorageService();
    }


    private String lockCountKey(String ip) {
        return String.format("lock-count-{}", ip);
    }

    private String lockKey(String ip) {
        return String.format("lock-duration-{}", ip);
    }
}

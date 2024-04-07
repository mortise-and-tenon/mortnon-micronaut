package fun.mortnon.service.login.impl;

import fun.mortnon.dal.sys.entity.SysConfig;
import fun.mortnon.dal.sys.entity.SysUser;
import fun.mortnon.dal.sys.entity.config.DoubleFactorType;
import fun.mortnon.framework.constants.LoginTypeConstants;
import fun.mortnon.framework.message.entity.MessageType;
import fun.mortnon.framework.properties.CommonProperties;
import fun.mortnon.framework.utils.IpUtil;
import fun.mortnon.service.login.LoginFactory;
import fun.mortnon.service.login.LoginService;
import fun.mortnon.service.login.LoginStorageService;
import fun.mortnon.service.sys.ConfigService;
import fun.mortnon.service.sys.EncryptService;
import fun.mortnon.service.sys.SysUserService;
import fun.mortnon.service.sys.message.MessageService;
import fun.mortnon.web.vo.login.DoubleFactor;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fun.mortnon.framework.constants.MessageConstants.VERIFY_CODE_PARAMETER_CODE;
import static fun.mortnon.framework.constants.MessageConstants.VERIFY_CODE_TEMPLATE;

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

    @Inject
    private MessageService messageService;

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

    @Override
    public boolean generateDoubleFactorCode(DoubleFactor doubleFactor) {
        SysConfig config = configService.queryConfig().block(Duration.ofSeconds(3));

        DoubleFactorType factor = config.getDoubleFactor();
        if (factor == DoubleFactorType.DISABLE) {
            return true;
        }

        MessageType type = MessageType.EMAIL;
        switch (factor) {
            case EMAIL:
                type = MessageType.EMAIL;
                break;
            default:
                break;
        }

        String code = RandomStringUtils.randomAlphanumeric(6);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put(VERIFY_CODE_PARAMETER_CODE, code);

        //缓存双因子验证码
        getStorageService().saveDoubleFactorCode(doubleFactor.getUsername(), code, commonProperties.getDoubleFactorTtl());

        return messageService.sendCode(type, doubleFactor.getUsername(), VERIFY_CODE_TEMPLATE, parameters);
    }

    @Override
    public boolean verifyCode(String userName, String code) {
        return getStorageService().validateDoubleFactorCode(userName, code);
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

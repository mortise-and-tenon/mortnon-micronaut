package fun.mortnon.service.login.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import fun.mortnon.framework.properties.CaptchaProperties;
import fun.mortnon.service.login.LoginStorageService;
import fun.mortnon.service.login.enums.LoginConstants;
import io.micronaut.security.token.jwt.generator.AccessTokenConfigurationProperties;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.concurrent.ExecutionException;

/**
 * @author dongfangzan
 * @date 27.4.21 4:17 下午
 */
@Slf4j
@Named(LoginConstants.LOCAL)
public class LocalLoginStorageServiceImpl implements LoginStorageService {
    @Inject
    private AccessTokenConfigurationProperties accessTokenConfigurationProperties;

    @Inject
    private CaptchaProperties captchaProperties;

    private LoadingCache<String, String> tokenCache;

    private LoadingCache<String, String> captchaCache;

    private final int MAX_CACHE_SIZE = 100;

    @PostConstruct
    public void init() {
        tokenCache = CacheBuilder.newBuilder()
                .maximumSize(MAX_CACHE_SIZE).expireAfterWrite(Duration.ofSeconds(accessTokenConfigurationProperties.getExpiration()))
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String s) throws Exception {
                        return "";
                    }
                });
        captchaCache = CacheBuilder.newBuilder()
                .maximumSize(MAX_CACHE_SIZE).expireAfterWrite(Duration.ofSeconds(captchaProperties.getExpireSeconds()))
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String s) throws Exception {
                        return "";
                    }
                });
    }

    @Override
    public boolean tokenIsExists(String token) {
        try {
            String s = tokenCache.get(token);
            return StringUtils.isNotEmpty(s);
        } catch (ExecutionException e) {
            return false;
        }
    }

    @Override
    public boolean saveToken(String token, int expiresSecond) {
        tokenCache.put(token, token);
        return true;
    }

    @Override
    public void deleteToken(String token) {
        tokenCache.invalidate(token);
    }

    @Override
    public boolean saveVerifyCode(String key, String code, long expiresSecond) {
        captchaCache.put(key, code);
        return true;
    }

    @Override
    public String getVerifyCode(String key) {
        try {
            return captchaCache.get(key);
        } catch (ExecutionException e) {
            return "";
        }
    }

    @Override
    public boolean deleteVerifyCode(String key) {
        captchaCache.invalidate(key);
        return true;
    }
}

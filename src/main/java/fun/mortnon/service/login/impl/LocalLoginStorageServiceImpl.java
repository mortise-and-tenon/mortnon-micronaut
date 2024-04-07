package fun.mortnon.service.login.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import fun.mortnon.framework.properties.CaptchaProperties;
import fun.mortnon.framework.properties.CommonProperties;
import fun.mortnon.service.login.LoginStorageService;
import fun.mortnon.service.login.enums.LoginConstants;
import io.micronaut.security.token.jwt.generator.AccessTokenConfigurationProperties;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.concurrent.ExecutionException;

import static fun.mortnon.service.login.enums.LoginConstants.DOUBLE_FACTOR_CODE;

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

    @Inject
    private CommonProperties commonProperties;

    private LoadingCache<String, String> tokenCache;

    private LoadingCache<String, String> captchaCache;

    private LoadingCache<String, String> rsaCache;

    private LoadingCache<String, String> lockCountCache;

    private LoadingCache<String, String> lockCache;

    private LoadingCache<String, String> doubleFactorCache;

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

        rsaCache = CacheBuilder.newBuilder()
                .maximumSize(MAX_CACHE_SIZE).expireAfterWrite(Duration.ofMinutes(commonProperties.getRsaTtl()))
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String s) throws Exception {
                        return "";
                    }
                });

        doubleFactorCache = CacheBuilder.newBuilder()
                .maximumSize(MAX_CACHE_SIZE).expireAfterWrite(Duration.ofSeconds(commonProperties.getDoubleFactorTtl()))
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

    @Override
    public String type() {
        return LoginConstants.LOCAL;
    }

    @Override
    public boolean saveRsaPublicKey(String publicKey, long expiresMinutes) {
        return saveRsa("COMMON", publicKey, expiresMinutes);
    }

    @Override
    public String getRsaPublicKey() {
        return getRsaPrivateKey("COMMON");
    }

    @Override
    public boolean saveRsa(String publicKey, String privateKey, long expiresMinutes) {
        rsaCache.put(publicKey, privateKey);
        return true;
    }

    @Override
    public String getRsaPrivateKey(String publicKey) {
        try {
            return captchaCache.get(publicKey);
        } catch (ExecutionException e) {
            return "";
        }
    }

    @Override
    public int saveLock(String key, long checkMinutes) {
        if (ObjectUtils.isEmpty(lockCountCache)) {
            refreshLockCache(checkMinutes);
        } else {
            if (lockCountCache.size() == 0) {
                refreshLockCache(checkMinutes);
            }
        }

        try {
            String count = lockCountCache.get(key);
            if (StringUtils.isEmpty(count)) {
                return 0;
            }

            int newCount = Integer.parseInt(count) + 1;
            lockCountCache.put(key, "" + newCount);
            return newCount;
        } catch (ExecutionException e) {
            lockCountCache.put(key, "0");
        }

        return 0;
    }

    private void refreshLockCache(long checkMinutes) {
        lockCountCache = CacheBuilder.newBuilder()
                .maximumSize(MAX_CACHE_SIZE).expireAfterWrite(Duration.ofMinutes(checkMinutes))
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String s) throws Exception {
                        return "";
                    }
                });
    }

    @Override
    public int getLock(String key) {
        try {
            return Integer.parseInt(lockCountCache.get(key));
        } catch (ExecutionException e) {
            return 0;
        }
    }

    @Override
    public boolean lockLogin(String key, long lockSeconds) {
        if (ObjectUtils.isEmpty(lockCache)) {
            lockCache = CacheBuilder.newBuilder()
                    .maximumSize(MAX_CACHE_SIZE).expireAfterWrite(Duration.ofMinutes(lockSeconds))
                    .build(new CacheLoader<String, String>() {
                        @Override
                        public String load(String s) throws Exception {
                            return "";
                        }
                    });
        }

        lockCache.put(key, "lock");
        return true;
    }

    @Override
    public long isLockLoginTimeExist(String key) {
        if (ObjectUtils.isEmpty(lockCache)) {
            return -2;
        }

        try {
            lockCache.get(key);
            return 1800;
        } catch (ExecutionException e) {
            return -2;
        }
    }

    @Override
    public boolean saveDoubleFactorCode(String userName, String code, long expiresSecond) {
        doubleFactorCache.put(String.format(DOUBLE_FACTOR_CODE, userName), code);
        return true;
    }

    @Override
    public boolean validateDoubleFactorCode(String userName, String code) {
        try {
            String storeCode = doubleFactorCache.get(String.format(DOUBLE_FACTOR_CODE, userName));
            return code.equals(storeCode);
        } catch (Exception e) {
            return false;
        }
    }
}

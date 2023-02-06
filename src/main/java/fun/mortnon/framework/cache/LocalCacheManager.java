package fun.mortnon.framework.cache;

import fun.mortnon.framework.properties.CaptchaProperties;
import fun.mortnon.framework.properties.JwtProperties;
import jakarta.inject.Singleton;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import javax.annotation.PostConstruct;
import java.time.Duration;

/**
 * session 缓存管理器
 * 缓存过期时间从配置中读取
 *
 * @author dongfangzan
 * @date 29.4.21 10:27 上午
 */
@Singleton
public class LocalCacheManager {

    private JwtProperties jwtProperties;

    private CaptchaProperties captchaProperties;

    private Cache<String, String> sessionCache;

    private Cache<String, String> captchaCache;

    private static final String SESSION_CACHE_NAME = "session";

    private static final String CAPTCHA_CACHE_NAME = "captcha";

    @PostConstruct
    public void init() {

        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);

        // 设置超时时间
        CacheConfigurationBuilder<String, String> configurationBuilder =
                CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(100))
                .withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofSeconds(jwtProperties.getExpireSecond())));

        CacheConfigurationBuilder<String, String> captchaConfigurationBuilder =
                CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(100))
                        .withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofSeconds(captchaProperties.getExpireSeconds())));

        sessionCache = cacheManager.createCache(SESSION_CACHE_NAME, configurationBuilder);

        captchaCache = cacheManager.createCache(CAPTCHA_CACHE_NAME, captchaConfigurationBuilder);
    }

    /**
     * 根据key获取缓存中存储的值
     *
     * @param key key
     * @return    缓存中的值
     */
    public String getValue(String key) {
        return sessionCache.get(key);
    }

    /**
     * 向缓存中写入数据，如果缓存中已存在，则返回存在的数据
     *
     * @param key   key
     * @param value value
     * @return      如果已存在，返回value
     */
    public String putValueIfAbsent(String key, String value) {
        return sessionCache.putIfAbsent(key, value);
    }

    /**
     * 向缓存中写入数据，存在则覆盖
     *
     * @param key   key
     * @param value value
     */
    public void putValue(String key, String value) {
        sessionCache.put(key, value);
    }

    /**
     * 删除缓存中的数据
     *
     * @param key value
     */
    public void remove(String key) {
        sessionCache.remove(key);
    }

    public Cache<String, String> getSessionCache() {
        return sessionCache;
    }

    public Cache<String, String> getCaptchaCache() {
        return captchaCache;
    }
}

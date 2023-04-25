package fun.mortnon.service.login;

import fun.mortnon.framework.properties.JwtProperties;
import fun.mortnon.service.login.enums.LoginConstants;
import fun.mortnon.service.login.enums.LoginStorageType;
import io.micronaut.context.BeanContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dongfangzan
 * @date 27.4.21 3:09 下午
 */
@Slf4j
@Singleton
public class LoginFactory {

    @Inject
    private JwtProperties jwtProperties;

    private final Map<String, LoginService> loginServiceMap = new ConcurrentHashMap<>();

    private final Map<String, LoginStorageService> loginStorageServiceMap = new ConcurrentHashMap<>();

    @Inject
    @Named(LoginConstants.LOCAL)
    private LoginStorageService localStorageService;

    @Inject
    @Named(LoginConstants.REDIS)
    private LoginStorageService redisStorageService;

    @Inject
    private BeanContext beanContext;

    @PostConstruct
    public void init() {
        // 初始化session存储
        for (LoginStorageService loginStorageService : beanContext.getBeansOfType(LoginStorageService.class)) {
            loginStorageServiceMap.put(loginStorageService.type(), loginStorageService);
        }
    }

    /**
     * 根据类型获取登录存储服务
     *
     * @param loginStorageType 登录存储类型
     * @return 登录存储服务
     */
    public LoginStorageService getLoginStorageService(LoginStorageType loginStorageType) {
        return loginStorageServiceMap.get(loginStorageType.getCode());
    }


    /**
     * 获取当前系统配置的登录存储服务
     *
     * @return 登录存储服务
     */
    public LoginStorageService getConfigLoginStorageService() {
        String loginStorageType = jwtProperties.getLoginStorageType();
        return loginStorageServiceMap.get(LoginStorageType.getByType(loginStorageType).getCode());
    }
}

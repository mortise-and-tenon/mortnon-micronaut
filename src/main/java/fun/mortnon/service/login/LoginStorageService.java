package fun.mortnon.service.login;

import fun.mortnon.service.login.model.JwtToken;
import fun.mortnon.service.login.model.LoginUser;
import org.apache.shiro.authz.SimpleAuthorizationInfo;

/**
 * @author dongfangzan
 * @date 27.4.21 4:16 下午
 */
public interface LoginStorageService {

    /**
     * 根据用户名获取登录用户
     *
     * @param username 用户名
     * @return         登录用户
     */
    LoginUser getLoginUserByName(String username);

    /**
     * 根据LoginUser构建权限信息
     *
     * @param loginUser 登录用户
     * @return 权限信息
     */
    SimpleAuthorizationInfo buildAuthorizationInfo(LoginUser loginUser);

    /**
     * 判断token 是否存在
     *
     * @param token token
     * @return      true-存在 false-不存在
     */
    JwtToken exists(String token);

    /**
     * 从缓存中获取cache
     *
     * @param username 用户名
     * @return         获取加密盐
     */
    String getSaltFromCache(String username);


    /**
     * 保存token信息
     *
     * @param loginUser 登录用户
     * @param jwtToken  token
     */
    void saveToken(LoginUser loginUser, JwtToken jwtToken);

    /**
     * 刷新token
     *
     * @param oldToken    旧token
     * @param username    用户名
     * @param newJwtToken 新token
     */
    void refreshToken(String oldToken, String username, JwtToken newJwtToken);

    /**
     * 删除token
     *
     * @param token    token
     * @param username 用户名
     */
    void deleteToken(String token, String username);

    /**
     * 创建验证码
     *
     * @param key  key
     * @param code code
     */
    void saveVerifyCode(String key, String code);

    /**
     * 删除验证码
     *
     * @param key code
     */
    void deleteVerifyCode(String key);

    /**
     * 验证验证码
     *
     * @param  key code
     * @return verify code
     */
    String getVerifyCode(String key);
}

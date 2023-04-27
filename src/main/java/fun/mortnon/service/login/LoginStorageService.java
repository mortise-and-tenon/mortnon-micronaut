package fun.mortnon.service.login;

/**
 * @author dev2007
 * @date 2023/2/10
 */
public interface LoginStorageService {
    /**
     * Token 是否存在于缓存
     *
     * @param token
     * @return
     */
    boolean tokenIsExists(String token);

    /**
     * 缓存 Token
     *
     * @param token         token 值
     * @param expiresSecond token 过期时长
     * @return
     */
    boolean saveToken(String token, int expiresSecond);

    /**
     * 删除 Token
     *
     * @param token
     */
    void deleteToken(String token);

    /**
     * 保存验证码
     *
     * @param key           验证码键
     * @param code          验证码值
     * @param expiresSecond 缓存时长（秒）
     * @return true. 保存成功
     */
    boolean saveVerifyCode(String key, String code, long expiresSecond);

    /**
     * 获取缓存验证码
     *
     * @param key
     * @return
     */
    String getVerifyCode(String key);

    /**
     * 删除缓存验证码
     *
     * @param key
     * @return
     */
    boolean deleteVerifyCode(String key);

    /**
     * 存储类型
     *
     * @return
     */
    String type();
}

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

    /**
     * 缓存一个公钥，避免反复生成新的密钥
     *
     * @param publicKey
     * @param expiresMinutes
     * @return
     */
    boolean saveRsaPublicKey(String publicKey, long expiresMinutes);

    /**
     * 获取公钥
     *
     * @return
     */
    String getRsaPublicKey();

    /**
     * 保存 RSA 公私钥
     *
     * @param publicKey
     * @param privateKey
     * @param expiresMinutes
     * @return
     */
    boolean saveRsa(String publicKey, String privateKey, long expiresMinutes);

    /**
     * 获取 RSA 私钥
     *
     * @param publicKey
     * @return
     */
    String getRsaPrivateKey(String publicKey);
}

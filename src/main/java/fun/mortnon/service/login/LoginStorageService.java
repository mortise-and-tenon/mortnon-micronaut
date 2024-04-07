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

    /**
     * 增加指定key的锁定前的错误计数
     *
     * @param key
     * @param checkMinutes 锁定计数检查周期
     * @return 返回当前计数
     */
    int saveLock(String key, long checkMinutes);

    /**
     * 读取当前
     *
     * @param key
     * @return
     */
    int getLock(String key);

    /**
     * 添加锁定记录
     *
     * @param key
     * @param lockSeconds
     * @return
     */
    boolean lockLogin(String key, long lockSeconds);

    /**
     * 查询锁定剩余时长
     *
     * @param key
     * @return -2:未锁定；正数：锁定剩余时长
     */
    long isLockLoginTimeExist(String key);

    /**
     * 保存双因子验证码
     *
     * @param userName
     * @param code
     * @param expiresSecond
     * @return
     */
    boolean saveDoubleFactorCode(String userName, String code, long expiresSecond);

    /**
     * 校验双因子验证码是否一致
     *
     * @param userName 用户名
     * @param code     比较的验证码
     * @return
     */
    boolean validateDoubleFactorCode(String userName, String code);
}

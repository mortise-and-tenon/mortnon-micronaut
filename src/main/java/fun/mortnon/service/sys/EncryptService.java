package fun.mortnon.service.sys;

/**
 * @author dev2007
 * @date 2024/3/16
 */
public interface EncryptService {
    /**
     * 获取缓存的公钥
     *
     * @return
     */
    String getPublicKey();

    /**
     * 生成一对 RSA 公私钥
     *
     * @return 生成的公钥
     */
    String generateRSA();

    /**
     * 按给定的公钥查找对应的私钥，解密数据
     *
     * @param content
     * @param publicKey
     * @return
     */
    String decryptByRSA(String content, String publicKey);

    /**
     * 按给定私钥解密
     *
     * @param content
     * @param privateKeyStr
     * @return
     */
    String decryptByPrivateKey(String content, String privateKeyStr);

    /**
     * 使用缓存的公钥对应的私钥解密
     *
     * @param content
     * @return
     */
    String decryptByCacheKey(String content);
}

package fun.mortnon.service.sys.impl;

import fun.mortnon.framework.properties.CommonProperties;
import fun.mortnon.service.login.LoginFactory;
import fun.mortnon.service.login.LoginStorageService;
import fun.mortnon.service.sys.EncryptService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import static fun.mortnon.framework.constants.MortnonConstants.RSA_ALGORITHM;

/**
 * @author dev2007
 * @date 2024/3/16
 */
@Singleton
@Slf4j
public class EncryptServiceImpl implements EncryptService {
    @Inject
    private CommonProperties commonProperties;

    @Inject
    private LoginFactory loginFactory;

    private KeyPairGenerator keyPairGenerator;

    private KeyFactory keyFactory;

    private Cipher cipher;

    public EncryptServiceImpl() {
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
            keyPairGenerator.initialize(1024);
            keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        } catch (Exception e) {
            log.warn("init Encrypt object fail:", e);
        }
    }

    @Override
    public String getPublicKey() {
        String cacheKey = getStorageService().getRsaPublicKey();
        if (StringUtils.isNotEmpty(cacheKey)) {
            return cacheKey;
        }
        return "";
    }


    @Override
    public String generateRSA() {
        String cacheKey = getStorageService().getRsaPublicKey();
        if (StringUtils.isNotEmpty(cacheKey)) {
            return cacheKey;
        }

        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        String publicKeyStr = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String privateKeyStr = Base64.getEncoder().encodeToString(privateKey.getEncoded());

        getStorageService().saveRsaPublicKey(publicKeyStr, commonProperties.getRsaTtl());
        getStorageService().saveRsa(publicKeyStr, privateKeyStr, commonProperties.getRsaTtl());

        return publicKeyStr;
    }

    @Override
    public String decryptByRSA(String content, String publicKey) {
        String privateKeyStr = getStorageService().getRsaPrivateKey(publicKey);

        if (StringUtils.isEmpty(privateKeyStr)) {
            return "";
        }

        return decryptByPrivateKey(content, privateKeyStr);
    }

    @Override
    public String decryptByPrivateKey(String content, String privateKeyStr) {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyStr));
        try {
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] decryptContent = cipher.doFinal(Base64.getDecoder().decode(content));

            return new String(decryptContent, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("Decrypt fail:", e);
            return "";
        }
    }

    @Override
    public String decryptByCacheKey(String content) {
        return decryptByRSA(content, getPublicKey());
    }

    private LoginStorageService getStorageService() {
        return loginFactory.getConfigLoginStorageService();
    }
}

package fun.mortnon.service.login.impl;

import fun.mortnon.service.login.LoginStorageService;
import fun.mortnon.service.login.enums.LoginConstants;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;

/**
 * @author dongfangzan
 * @date 27.4.21 4:17 下午
 */
@Slf4j
@Named(LoginConstants.LOCAL)
public class LocalLoginStorageServiceImpl implements LoginStorageService {
    @Override
    public boolean tokenIsExists(String token) {
        return false;
    }

    @Override
    public boolean saveToken(String token, int expiresSecond) {
        return false;
    }

    @Override
    public boolean saveVerifyCode(String key, String code, long expiresSecond) {
        return false;
    }

    @Override
    public String getVerifyCode(String key) {
        return null;
    }

    @Override
    public boolean deleteVerifyCode(String key) {
        return false;
    }
}

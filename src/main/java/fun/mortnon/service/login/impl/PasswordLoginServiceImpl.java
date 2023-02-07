package fun.mortnon.service.login.impl;

import fun.mortnon.dal.sys.domain.SysUser;
import fun.mortnon.framework.constants.LoginTypeConstants;
import fun.mortnon.service.login.LoginService;
import fun.mortnon.service.login.enums.LoginType;
import fun.mortnon.service.login.model.LoginUser;
import fun.mortnon.service.sys.SysUserService;
import io.micronaut.security.authentication.AuthenticationRequest;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

/**
 * @author dongfangzan
 * @date 27.4.21 4:18 下午
 */
@Singleton
@Slf4j
@Named(LoginTypeConstants.PASSWORD)
public class PasswordLoginServiceImpl implements LoginService {

    @Inject
    private SysUserService sysUserService;

    @Override
    public boolean authenticate(AuthenticationRequest<?, ?> authenticationRequest) {
        String userName = (String) authenticationRequest.getIdentity();
        String password = (String) authenticationRequest.getSecret();
        SysUser sysUser = sysUserService.getUserByUsername(userName);

        if (null == sysUser) {
            log.info("user {} is not exists.", userName);
            return false;
        }

        boolean auth = auth(sysUser, password);
        if (!auth) {
            return false;
        }

        return true;
    }

    private boolean auth(SysUser sysUser, String password) {
        return sysUser.getPassword().equals(password);
    }
}

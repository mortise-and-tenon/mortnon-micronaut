package fun.mortnon.service.login;

import fun.mortnon.service.login.enums.LoginType;
import fun.mortnon.service.login.model.JwtToken;
import fun.mortnon.service.login.model.LoginUser;

/**
 * @author dongfangzan
 * @date 27.4.21 3:09 下午
 */
public interface LoginService {

    /**
     * 认证后的登录，调用该方法完成最后的登录流程
     *
     * @param authorizedUser 已经认证过后的用户
     * @param loginType      登录类型
     * @return               token
     */
    JwtToken login(LoginUser authorizedUser, LoginType loginType);
}

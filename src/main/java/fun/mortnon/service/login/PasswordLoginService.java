package fun.mortnon.service.login;

import fun.mortnon.service.login.model.JwtToken;

/**
 * @author dongfangzan
 * @date 27.4.21 5:15 下午
 */
public interface PasswordLoginService extends LoginService{

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @return         token
     */
    JwtToken authAndLogin(String username, String password);
}

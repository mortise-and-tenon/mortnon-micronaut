package fun.mortnon.service.login.enums;

/**
 * @author dongfangzan
 * @date 28.4.21 10:43 上午
 */
public class LoginConstants {

    public static final String REDIS = "Redis";

    public static final String LOCAL = "Local";

    /**
     * 登录用户token信息key
     * login:token:tokenMd5
     */
    public static final String LOGIN_TOKEN = "login:token:%s";

    /**
     * 登录用户信息key
     * login:user:username
     */
    public static final String LOGIN_USER = "login:user:%s";

    /**
     * 登录用户盐值信息key
     * login:salt:username
     */
    public static final String LOGIN_SALT = "login:salt:%s";

    /**
     * 验证码
     */
    public static final String VERIFY_CODE = "verify:code:%s";

    /**
     * RSA
     */
    public static final String RSA_CODE = "rsa:public:%s";

    /**
     * 双因子
     */
    public static final String DOUBLE_FACTOR_CODE = "double_factor:code:%s";

    /**
     * 不合法的token状态码
     */
    public static final int SC_INVALID_TOKEN = 461;

    /**
     * 刷新token状态码
     */
    public static final int SC_JWT_REFRESH_TOKEN = 460;

    /**
     * 静态图片
     */
    public static final String CAPTCHA_TYPE_DEFAULT = "png";

    /**
     * gif
     */
    public static final String CAPTCHA_TYPE_GIF = "gif";

    /**
     * 中文
     */
    public static final String CAPTCHA_TYPE_CHINESE = "chinese";
}

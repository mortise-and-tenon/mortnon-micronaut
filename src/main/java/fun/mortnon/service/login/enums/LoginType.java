package fun.mortnon.service.login.enums;

import fun.mortnon.framework.constants.LoginTypeConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 *
 * 登录类型
 *
 * @author dongfangzan
 * @date 27.4.21 3:11 下午
 */
public enum LoginType {
    /** 用户名密码 */
    PASSWORD(LoginTypeConstants.PASSWORD, "用户名密码登录"),

    /** 手机验证码 */
    PHONE("phone", "手机验证码登录"),

    /** oauth登录 */
    OAUTH("oauth", "oauth登录"),

    /** 微信登录 */
    WECHAT("wechat", "微信登录"),

    /** 支付宝登录 */
    ALIPAY("alipay", "支付宝登录"),

    /** 微博登录 */
    WEIBO("weibo", "微博登录"),

    /** QQ登录 */
    QQ("qq", "qq登录")

    ;

    /** 登录 */
    private String code;

    private String description;

    LoginType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static LoginType getByType(String type) {
        if (StringUtils.isBlank(type)) {
            return null;
        }

        return Arrays.stream(LoginType.values()).filter(typeEnum -> typeEnum.code.equals(type))
                .findAny().orElse(null);
    }
}

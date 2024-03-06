package fun.mortnon.framework.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * 错误码
 *
 * @author dongfangzan
 * @date 14.4.21 9:59 上午
 */
public enum ErrorCodeEnum {

    /**
     * 成功
     */
    SUCCESS("00000", "success"),

    /**
     * 用户端错误
     */
    USER_ERROR("A0001", "user end error"),

    /**
     * 参数异常
     */
    PARAM_ERROR("A0002", "param error"),

    /**
     * 用户注册错误
     */
    USER_REGISTER_ERROR("B0001", "user register error"),

    /**
     * 用户未统一隐私协议
     */
    DID_NOT_AGREE_PRIVACY_AGREEMENT("A0101", "user did not agree to privacy agreement"),

    /**
     * 用户名校验失败
     */
    USER_NAME_CHECK_FAILED("A0110", "username check failed"),

    /**
     * 用户名已存在
     */
    USERNAME_ALREADY_EXISTS("A0111", "username already exists"),

    /**
     * 用户身份校验失败
     */
    USER_IDENTITY_CHECK_FAILED("A0120", "user identity check failed"),

    /**
     * 用户名或密码错误
     */
    INVALID_USERNAME_OR_PASSWORD("A0210", "Invalid username or password", "result.login.userpwd.fail"),

    /**
     * 用户登录过期
     */
    USER_LOGIN_EXPIRED("A0230", "user login time expired"),

    /**
     * 验证码错误
     */
    VERIFY_CODE_ERROR("A0240", "Invalid verification code","result.login.code.fail"),

    /**
     * 访问未授权
     */
    UNAUTHORIZED_ACCESS("A0301", "unauthorized access"),

    /**
     * 用户无权限
     */
    FORBIDDEN("A0310", "forbidden access"),

    /**
     * token已经过期
     */
    TOKEN_EXPIRE("A0311", "token expire"),

    /**
     * 系统异常
     */
    SYSTEM_ERROR("B0001", "system error"),

    /**
     * 重复
     */
    REPEAT_ERROR("B0002", "repeat data"),

    /**
     * 不存在
     */
    NOT_EXISTS_ERROR("B0003", "data is not exists"),

    /**
     * 数据已被使用
     */
    USED_DATA_ERROR("B0004", "data is used");

    ErrorCodeEnum(String errorCode, String description) {
        this.errorCode = errorCode;
        this.description = description;
        this.i18n = description;
    }

    ErrorCodeEnum(String errorCode, String description, String i18n) {
        this.errorCode = errorCode;
        this.description = description;
        this.i18n = i18n;
    }

    /**
     * 错误码
     */
    @JsonValue
    private final String errorCode;

    /**
     * 描述信息
     */
    private final String description;

    /**
     * 国际化代码串
     */
    private final String i18n;

    /**
     * A级错误
     */
    public static final String A_LEVEL = "A";

    /**
     * B级错误
     */
    public static final String B_LEVEL = "B";

    /**
     * 是否A级错误
     *
     * @return true-是
     */
    public boolean isA() {
        return getErrorCode().startsWith(A_LEVEL);
    }

    /**
     * 获取错误码
     *
     * @return 如A0001
     */
    public String getErrorCode() {
        return this.errorCode;
    }

    /**
     * 根据错误码获取枚举
     *
     * @param errorCode 错误码，如A0001
     * @return 枚举
     */
    public static ErrorCodeEnum getByErrorCode(String errorCode) {

        if (StringUtils.isBlank(errorCode)) {
            return null;
        }

        return Arrays.stream(ErrorCodeEnum.values())
                .filter(errorCodeEnum -> errorCode.equals(errorCodeEnum.errorCode))
                .findFirst().orElse(null);
    }

    public String getDescription() {
        return description;
    }

    public String getI18n() {
        return i18n;
    }
}

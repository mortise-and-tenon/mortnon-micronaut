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
     * 用户名校验失败
     */
    USER_NAME_CHECK_FAILED("A0110", "Username verification failed"),

    /**
     * 用户名已存在
     */
    USERNAME_ALREADY_EXISTS("A0111", "Username already exists", "result.user.name.repeat.fail"),

    /**
     * 用户不存在
     */
    USER_NOT_EXISTS("A0112", "User does not exist", "result.user.not.exist.fail"),

    /**
     * 用户信息错误
     */
    USER_INFO_ERROR("A0113", "User information error", "result.user.info.error.fail"),

    /**
     * 两次输入密码不匹配
     */
    PASSWORD_NOT_MATCH("A0114", "The two entered passwords do not match", "result.user.password.not.match.fail"),

    /**
     * 旧密码为空
     */
    OLD_PASSWORD_IS_EMPTY("A0115", "Old password is empty", "result.user.password.empty.fail"),

    /**
     * 默认用户禁止删除
     */
    DEFAULT_USER_FORBID_DELETE("A0116", "Default user data can't be deleted", "result.user.default.cant.delete.fail"),

    /**
     * 默认用户禁止变更
     */
    DEFAULT_USER_FORBID_UPDATE("A0117", "Changing default user is prohibited", "result.user.default.forbid.fail"),

    /**
     * 上传失败
     */
    UPLOAD_FAIL("A0118", "Upload failed", "result.upload.fail"),


    /**
     * 用户身份校验失败
     */
    USER_IDENTITY_CHECK_FAILED("A0120", "user identity check failed"),

    /**
     * 组织或角色信息错误
     */
    PROJECT_ROLE_ERROR("A0130", "Organization or role information error", "result.user.role.project.empty.fail"),

    /**
     * 组织不存在
     */
    PROJECT_NOT_EXIST("A0131", "Organization does not exist", "result.project.not.exist.fail"),

    /**
     * 组织名重复
     */
    PROJECT_REPEAT("A0132", "Department name duplication", "result.project.name.repeat.fail"),

    /**
     * 组织数据错误
     */
    PROJECT_ERROR("A0133", "Department data error", "result.project.data.invalid.fail"),

    /**
     * 父组织不存在
     */
    PARENT_PROJECT_NOT_EXIST("A0134", "Parent department does not exist", "result.project.parent.not.exist.fail"),

    /**
     * 默认组织禁止删除
     */
    DEFAULT_PROJECT_FORBID_DELETE("A0135", "Default department data can't be deleted", "result.project.default.cant.delete.fail"),

    /**
     * 组织已被使用
     */
    PROJECT_USED("A0136", "Department has already been used", "result.project.used.fail"),

    /**
     * 子组织已被使用
     */
    CHILD_PROJECT_USED("A0137", "Sub-departments in use", "result.project.child.delete.used.fail"),

    /**
     * 角色不存在
     */
    ROLE_NOT_EXIST("A0140", "Role does not exist", "result.role.not.exist.fail"),

    /**
     * 角色名字重复
     */
    ROLE_NAME_REPEAT("A0141", "Role data duplication", "result.role.repeat.fail"),

    /**
     * 默认角色禁止删除
     */
    DEFAULT_ROLE_FORBID_DELETE("A0142", "Default role data can't be deleted", "result.role.default.cant.delete.fail"),

    /**
     * 角色已被使用
     */
    ROLE_USED("A0143", "Role has already been used", "result.role.used.fail"),


    /**
     * 角色数据错误
     */
    ROLE_ERROR("A0144", "Role does not exist", "result.role.data.invalid.fail"),


    /**
     * 权限数据不存在
     */
    PERMISSION_ERROR("A0150", "Permission data error", "result.permission.id.not.exist.fail"),

    /**
     * 权限名字重复
     */
    PERMISSION_NAME_REPEAT("A0151", "Permission name duplication", "result.permission.repeat.fail"),

    /**
     * 权限被使用
     */
    PERMISSION_USED("A0152", "Permission has already been used", "result.permission.used.fail"),

    /**
     * API 已注册
     */
    API_REGISTERED("A0153", "API has already been registered", "result.api.registered.fail"),

    /**
     * 组织角色数据错误
     */
    PROJECT_ROLE_DATA_ERROR("A0160", "Organization or role data error", "result.project.role.error.fail"),

    /**
     * 用户名或密码错误
     */
    INVALID_USERNAME_OR_PASSWORD("A0210", "Invalid username or password", "result.login.userpwd.fail"),

    /**
     * 认证信息为空
     */
    AUTH_EMPTY("A0211", "Authentication information is empty", "result.auth.empty.fail"),

    /**
     * 用户已停用
     */
    USER_FORBIDDEN("A0212", "result.login.user.forbidden.fail", "result.login.user.forbidden.fail"),

    /**
     * 用户异常
     */
    USER_DATA_ERROR("A0213", "User exception", "result.login.user.error.fail"),

    /**
     * 登录锁定
     */
    LOGIN_LOCK("A0214", "Retry count exceeded, waiting for lock to be released", "result.login.userpwd.lock.fail"),

    /**
     * 用户登录过期
     */
    USER_LOGIN_EXPIRED("A0230", "user login time expired"),

    /**
     * 验证码错误
     */
    VERIFY_CODE_ERROR("A0240", "Invalid verification code", "result.login.code.fail"),

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
    SYSTEM_ERROR("B0001", "System exception", "result.system.error"),

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
    USED_DATA_ERROR("B0004", "The data has been used","result.data.used.fail"),

    /**
     * 文件异常
     */
    FILE_CONTENT_ERROR("B0005", "File format is incorrect", "result.file.content.error"),

    /**
     * 必填数据为空
     */
    DATA_EMPTY("B0006", "Required data is empty", "result.file.upload.empty.fail");

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

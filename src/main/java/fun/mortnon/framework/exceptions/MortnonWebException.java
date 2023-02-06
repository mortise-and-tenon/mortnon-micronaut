package fun.mortnon.framework.exceptions;

import fun.mortnon.framework.enums.ErrorCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;

/**
 * @author dongfangzan
 * @date 28.4.21 2:40 下午
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class MortnonWebException extends AuthenticationException implements MortnonException {

    /**
     * uid
     */
    private static final long serialVersionUID = 1992908203816666196L;

    /** 错误码 */
    private String errorCode;

    /** 错误码枚举信息 */
    private ErrorCodeEnum errorCodeEnum;

    /** 错误信息 */
    private String message;

    /**
     * 有引起错误原因的错误
     *
     * @param errorCodeEnum 错误码枚举
     * @param cause         原因
     */
    public MortnonWebException(ErrorCodeEnum errorCodeEnum, Throwable cause) {
        super(cause.getMessage(), cause);
        this.errorCodeEnum = errorCodeEnum;
        this.message = cause.getMessage();

        if (StringUtils.isBlank(message)) {
            this.message = errorCodeEnum.getDescription();
        }
    }

    /**
     * 内部生成的错误原因
     *
     * @param errorCodeEnum 错误码枚举
     * @param message       错误信息
     */
    public MortnonWebException(ErrorCodeEnum errorCodeEnum, String message) {
        super(message);
        this.errorCodeEnum = errorCodeEnum;
        this.message = message;

        if (StringUtils.isBlank(message)) {
            this.message = errorCodeEnum.getDescription();
        }
    }

    @Override
    public ErrorCodeEnum getErrorCodeEnum() {
        return errorCodeEnum;
    }

    @Override
    public String errorCode() {
        return errorCode;
    }

    @Override
    public String message() {
        return message;
    }
}

package fun.mortnon.framework.exceptions;

import fun.mortnon.framework.enums.ErrorCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

/**
 * @author dongfangzan
 * @date 14.4.21 10:50 上午
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class MortnonBaseException extends RuntimeException implements MortnonException {

    /**
     * uid
     */
    private static final long serialVersionUID = 1992908203816666196L;

    /**
     * 有引起错误原因的错误
     *
     * @param errorCodeEnum 错误码枚举
     * @param cause         原因
     */
    public MortnonBaseException(ErrorCodeEnum errorCodeEnum, Throwable cause) {
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
    public MortnonBaseException(ErrorCodeEnum errorCodeEnum, String message) {
        super(message);
        this.errorCodeEnum = errorCodeEnum;
        this.message = message;

        if (StringUtils.isBlank(message)) {
            this.message = errorCodeEnum.getDescription();
        }
    }

    public MortnonBaseException(ErrorCodeEnum errorCodeEnum) {
        this(errorCodeEnum, "");
    }

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误码枚举信息
     */
    private ErrorCodeEnum errorCodeEnum;

    /**
     * 错误信息
     */
    private String message;

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

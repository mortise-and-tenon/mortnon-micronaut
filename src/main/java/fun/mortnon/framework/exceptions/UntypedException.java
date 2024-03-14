package fun.mortnon.framework.exceptions;

import fun.mortnon.framework.enums.ErrorCodeEnum;

/**
 * 未分类的异常
 *
 * @author dev2007
 * @date 2024/3/14
 */
public class UntypedException extends MortnonBaseException {
    public UntypedException(ErrorCodeEnum errorCodeEnum, Throwable cause) {
        super(errorCodeEnum, cause);
    }

    public UntypedException(ErrorCodeEnum errorCodeEnum, String message) {
        super(errorCodeEnum, message);
    }

    public UntypedException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum);
    }

    public static UntypedException create(ErrorCodeEnum errorCodeEnum) {
        return new UntypedException(errorCodeEnum);
    }
}

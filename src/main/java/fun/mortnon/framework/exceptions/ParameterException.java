package fun.mortnon.framework.exceptions;

import fun.mortnon.framework.enums.ErrorCodeEnum;

/**
 * @author dev2007
 * @date 2023/2/21
 */
public class ParameterException extends MortnonBaseException{
    private ParameterException(ErrorCodeEnum errorCodeEnum, Throwable cause) {
        super(errorCodeEnum, cause);
    }

    private ParameterException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum);
    }

    private ParameterException(ErrorCodeEnum errorCodeEnum, String message) {
        super(errorCodeEnum, message);
    }

    public static ParameterException create(ErrorCodeEnum errorCodeEnum) {
        return new ParameterException(errorCodeEnum);
    }

    public static ParameterException create() {
        return new ParameterException(ErrorCodeEnum.PARAM_ERROR);
    }

    public static ParameterException create(String message) {
        return new ParameterException(ErrorCodeEnum.PARAM_ERROR, message);
    }

    public static ParameterException create(Throwable cause) {
        return new ParameterException(ErrorCodeEnum.PARAM_ERROR, cause);
    }
}

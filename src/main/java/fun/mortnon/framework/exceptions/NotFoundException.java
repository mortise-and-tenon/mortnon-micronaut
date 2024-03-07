package fun.mortnon.framework.exceptions;

import fun.mortnon.framework.enums.ErrorCodeEnum;

/**
 * @author dev2007
 * @date 2023/2/21
 */
public class NotFoundException extends MortnonBaseException {
    private NotFoundException(ErrorCodeEnum errorCodeEnum, Throwable cause) {
        super(errorCodeEnum, cause);
    }

    private NotFoundException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum);
    }

    private NotFoundException(ErrorCodeEnum errorCodeEnum, String message) {
        super(errorCodeEnum, message);
    }

    public static NotFoundException create(ErrorCodeEnum errorCodeEnum) {
        return new NotFoundException(errorCodeEnum);
    }

    public static NotFoundException create() {
        return new NotFoundException(ErrorCodeEnum.NOT_EXISTS_ERROR);
    }

    public static NotFoundException create(String message) {
        return new NotFoundException(ErrorCodeEnum.NOT_EXISTS_ERROR, message);
    }

    public static NotFoundException create(Throwable cause) {
        return new NotFoundException(ErrorCodeEnum.NOT_EXISTS_ERROR, cause);
    }
}

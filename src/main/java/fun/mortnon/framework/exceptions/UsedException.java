package fun.mortnon.framework.exceptions;

import fun.mortnon.framework.enums.ErrorCodeEnum;

/**
 * @author dev2007
 * @date 2023/2/24
 */
public class UsedException extends MortnonBaseException{
    private UsedException(ErrorCodeEnum errorCodeEnum, Throwable cause) {
        super(errorCodeEnum, cause);
    }

    private UsedException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum);
    }

    private UsedException(ErrorCodeEnum errorCodeEnum, String message) {
        super(errorCodeEnum, message);
    }

    public static UsedException create() {
        return new UsedException(ErrorCodeEnum.USED_DATA_ERROR);
    }

    public static UsedException create(ErrorCodeEnum errorCodeEnum) {
        return new UsedException(errorCodeEnum);
    }

    public static UsedException create(ErrorCodeEnum errorCodeEnum, String message) {
        return new UsedException(errorCodeEnum, message);
    }

    public static UsedException create(String message) {
        return new UsedException(ErrorCodeEnum.USED_DATA_ERROR, message);
    }

    public static UsedException create(Throwable cause) {
        return new UsedException(ErrorCodeEnum.USED_DATA_ERROR, cause);
    }
}

package fun.mortnon.framework.exceptions;

import fun.mortnon.framework.enums.ErrorCodeEnum;
import lombok.Data;

/**
 * @author dev2007
 * @date 2023/2/21
 */
public class RepeatDataException extends MortnonBaseException {
    private Object attachData;

    private RepeatDataException(ErrorCodeEnum errorCodeEnum, Throwable cause) {
        super(errorCodeEnum, cause);
    }

    private RepeatDataException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum);
    }

    private RepeatDataException(ErrorCodeEnum errorCodeEnum, String message) {
        super(errorCodeEnum, message);
    }

    public static RepeatDataException create() {
        return new RepeatDataException(ErrorCodeEnum.REPEAT_ERROR);
    }

    public static RepeatDataException create(ErrorCodeEnum errorCodeEnum) {
        return new RepeatDataException(errorCodeEnum);
    }

    public static RepeatDataException createWithData(ErrorCodeEnum errorCodeEnum, Object attachData) {
        RepeatDataException repeatDataException = new RepeatDataException(errorCodeEnum);
        repeatDataException.setAttachData(attachData);
        return repeatDataException;
    }

    public static RepeatDataException create(ErrorCodeEnum errorCodeEnum, String message) {
        return new RepeatDataException(errorCodeEnum, message);
    }

    public static RepeatDataException create(String message) {
        return new RepeatDataException(ErrorCodeEnum.REPEAT_ERROR, message);
    }

    public static RepeatDataException create(Throwable cause) {
        return new RepeatDataException(ErrorCodeEnum.REPEAT_ERROR, cause);
    }

    public Object getAttachData() {
        return attachData;
    }

    public void setAttachData(Object attachData) {
        this.attachData = attachData;
    }
}

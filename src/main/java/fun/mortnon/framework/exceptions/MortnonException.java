package fun.mortnon.framework.exceptions;

import fun.mortnon.framework.enums.ErrorCodeEnum;

/**
 * @author dongfangzan
 * @date 28.4.21 2:37 下午
 */
public interface MortnonException {

    /**
     * 错误码信息
     */
    ErrorCodeEnum getErrorCodeEnum();

    /**
     * 错误码
     */
    String errorCode();

    /**
     * 错误消息
     */
    String message();
}

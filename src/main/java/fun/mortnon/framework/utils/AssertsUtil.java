package fun.mortnon.framework.utils;

import fun.mortnon.framework.enums.ErrorCodeEnum;
import fun.mortnon.framework.exceptions.MortnonBaseException;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 服务断言工具
 *
 * @author dongfangzan
 * @date 14.4.21 10:44 上午
 */
public class AssertsUtil {

    /**
     * 断言为真
     *
     * @param isSuccess     表达式
     * @param errorCodeEnum 错误码
     * @param msg           消息
     */
    public static void assertTrue(boolean isSuccess, ErrorCodeEnum errorCodeEnum, String msg) {
        if (!isSuccess) {
            throwException(errorCodeEnum, msg);
        }
    }

    /**
     * 断言非空
     *
     * @param obj           对象
     * @param errorCodeEnum 错误码
     * @param msg           错误消息
     */
    public static void nonNull(Object obj, ErrorCodeEnum errorCodeEnum, String msg) {
        assertTrue(Objects.nonNull(obj), errorCodeEnum, msg);
    }

    /**
     * 断言字符串非空
     *
     * @param str           字符串
     * @param errorCodeEnum 错误码
     * @param msg           消息
     */
    public static void notBlank(String str, ErrorCodeEnum errorCodeEnum, String msg) {
        assertTrue(StringUtils.isNotBlank(str), errorCodeEnum, msg);
    }


    /**
     * 根据错误码抛出异常
     *
     * @param errorCodeEnum 错误码
     * @param msg           错误信息
     */
    protected static void throwException(ErrorCodeEnum errorCodeEnum, String msg) {
        throw new MortnonBaseException(errorCodeEnum, msg);
    }
}

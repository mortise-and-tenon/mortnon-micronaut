package fun.mortnon.framework.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * web层默认返回
 *
 * @author dongfangzan
 * @date 14.4.21 10:27 上午
 */
@Data
@Accessors(chain = true)
public class MortnonResult<T> implements Serializable {

    /**
     * uid
     */
    private static final long serialVersionUID = -853188663210091249L;

    /**
     * 结果
     */
    private T data;

    /**
     * 错误码
     * @mock 00000
     */
    private String errorCode;

    /**
     * 错误描述
     * @mock success
     */
    private String message;

    /**
     * 是否成功
     * @mock true
     */
    private boolean success;
}

package fun.mortnon.framework.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import fun.mortnon.framework.enums.ErrorCodeEnum;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.model.Page;
import io.micronaut.serde.annotation.Serdeable;
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
@Serdeable
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
     *
     * @mock 00000
     */
    @JsonProperty(value = "error_code")
    private String errorCode;

    /**
     * 错误描述
     *
     * @mock success
     */
    private String message;

    /**
     * 是否成功
     *
     * @mock true
     */
    private boolean success;

    public static <T> MortnonResult success(T data) {
        MortnonResult result = new MortnonResult();
        result.setData(data);
        result.setErrorCode(ErrorCodeEnum.SUCCESS.getErrorCode());
        result.setSuccess(true);
        return result;
    }

    public static MortnonResult successPageData(Page page) {
        PageableData pageableData = new PageableData(page.getPageNumber(), page.getTotalPages(), page.getSize(),
                page.getTotalSize(), page.getContent());
        return success(pageableData);
    }

    public static MortnonResult fail(ErrorCodeEnum errorCodeEnum) {
        return fail(errorCodeEnum, errorCodeEnum.getDescription());
    }

    public static MortnonResult fail(ErrorCodeEnum errorCodeEnum, String message) {
        MortnonResult result = new MortnonResult();
        result.setErrorCode(errorCodeEnum.getErrorCode());
        result.setMessage(message);
        result.setSuccess(false);
        return result;
    }
}

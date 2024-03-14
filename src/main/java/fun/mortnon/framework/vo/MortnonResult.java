package fun.mortnon.framework.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import fun.mortnon.framework.enums.ErrorCodeEnum;
import io.micronaut.context.MessageSource;
import io.micronaut.data.model.Page;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import jakarta.inject.Inject;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.annotation.PostConstruct;

/**
 * web层默认返回
 *
 * @author dongfangzan
 * @date 14.4.21 10:27 上午
 */
@Data
@Accessors(chain = true)
@Serdeable(naming = SnakeCaseStrategy.class)
public class MortnonResult<T> {

    /**
     * uid
     */
    private static final long serialVersionUID = -853188663210091249L;

    /**
     * 结果
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误描述
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    /**
     * 是否成功
     */
    private boolean success;

    public static <T> MortnonResult success(T data) {
        MortnonResult result = new MortnonResult();
        result.setData(data);
        result.setErrorCode(ErrorCodeEnum.SUCCESS.getErrorCode());
        result.setSuccess(true);
        return result;
    }

    public static MortnonResult success() {
        MortnonResult result = new MortnonResult();
        result.setErrorCode(ErrorCodeEnum.SUCCESS.getErrorCode());
        result.setSuccess(true);
        return result;
    }

    public static MortnonResult success(String message) {
        MortnonResult result = new MortnonResult();
        result.setErrorCode(ErrorCodeEnum.SUCCESS.getErrorCode());
        result.setSuccess(true);
        return result;
    }

    public static <T> MortnonResult success(T data, String message) {
        MortnonResult result = new MortnonResult();
        result.setData(data);
        result.setErrorCode(ErrorCodeEnum.SUCCESS.getErrorCode());
        result.setMessage(message);
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

    public static MortnonResult fail(ErrorCodeEnum errorCodeEnum, String message, Object data) {
        MortnonResult result = new MortnonResult();
        result.setErrorCode(errorCodeEnum.getErrorCode());
        result.setMessage(message);
        result.setSuccess(false);
        result.setData(data);
        return result;
    }
}

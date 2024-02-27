package fun.mortnon.service.login.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author dongfangzan
 * @date 30.4.21 10:27 上午
 */
@Data
@Accessors(chain = true)
@Serdeable
public class MortnonCaptcha {

    /**
     * 是否启用验证码
     */
    private boolean enabled;

    /** key */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String key;

    /** 图片 */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String image;
}

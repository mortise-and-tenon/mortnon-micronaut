package fun.mortnon.service.login.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 验证码对象
 *
 * @author dongfangzan
 * @date 30.4.21 10:27 上午
 */
@Data
@Accessors(chain = true)
@Serdeable
public class MortnonCaptcha {

    /**
     * 是否启用验证码功能
     */
    private boolean enabled;

    /**
     * 验证码的 key
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String key;

    /**
     * 验证码图片
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String image;
}

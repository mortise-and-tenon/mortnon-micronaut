package fun.mortnon.service.login.model;

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

    /** key */
    private String captchaKey;

    /** 图片 */
    private String captchaImage;
}

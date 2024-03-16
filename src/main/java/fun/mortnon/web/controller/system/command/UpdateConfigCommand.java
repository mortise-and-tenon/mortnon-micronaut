package fun.mortnon.web.controller.system.command;

import fun.mortnon.dal.sys.entity.config.CaptchaType;
import fun.mortnon.dal.sys.entity.config.DoubleFactorType;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

/**
 * 修改系统配置
 *
 * @author dev2007
 * @date 2024/3/15
 */
@Introspected
@Serdeable(naming = SnakeCaseStrategy.class)
@Data
public class UpdateConfigCommand {
    /**
     * 验证码类型
     */
    private CaptchaType captcha;

    /**
     * 是否密码加密传输
     */
    private Boolean passwordEncrypt;

    /**
     * 双因子类型
     */
    private DoubleFactorType doubleFactor;
}

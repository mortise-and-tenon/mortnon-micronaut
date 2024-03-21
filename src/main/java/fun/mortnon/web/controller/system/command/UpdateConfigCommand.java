package fun.mortnon.web.controller.system.command;

import fun.mortnon.dal.sys.entity.config.CaptchaType;
import fun.mortnon.dal.sys.entity.config.DoubleFactorType;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

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
     * 密码重试次数
     */
    @Positive
    @Min(0)
    @Max(10)
    private Integer tryCount;

    /**
     * 锁定时长
     */
    @Positive
    @Min(1)
    private Integer lockTime;

    /**
     * 双因子类型
     */
    private DoubleFactorType doubleFactor;
}

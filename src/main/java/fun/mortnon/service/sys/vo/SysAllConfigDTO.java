package fun.mortnon.service.sys.vo;

import fun.mortnon.dal.sys.entity.SysConfig;
import fun.mortnon.dal.sys.entity.config.CaptchaType;
import fun.mortnon.dal.sys.entity.config.DoubleFactorType;
import fun.mortnon.framework.utils.MortnonBeanUtils;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统配置
 *
 * @author dev2007
 * @date 2024/3/15
 */
@Introspected
@Serdeable(naming = SnakeCaseStrategy.class)
@Data
public class SysAllConfigDTO {
    /**
     * 是否开启验证码
     */
    private CaptchaType captcha;

    /**
     * 验证码可选类型
     */
    private List<String> captchaTypes = EnumSet.allOf(CaptchaType.class).stream()
            .map(Enum::name).collect(Collectors.toList());

    /**
     * 是否密码加密传输
     */
    private boolean passwordEncrypt;

    /**
     * 是否开启双因子认证
     */
    private DoubleFactorType doubleFactor;

    /**
     * 双因子可选类型
     */
    private List<String> doubleFactorTypes = EnumSet.allOf(DoubleFactorType.class)
            .stream().map(Enum::name).collect(Collectors.toList());

    public static SysAllConfigDTO convert(SysConfig sysConfig) {
        SysAllConfigDTO sysConfigDTO = new SysAllConfigDTO();
        MortnonBeanUtils.copy(sysConfig, sysConfigDTO);
        return sysConfigDTO;
    }
}

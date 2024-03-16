package fun.mortnon.service.sys.vo;

import fun.mortnon.dal.sys.entity.SysConfig;
import fun.mortnon.framework.utils.MortnonBeanUtils;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

/**
 * @author dev2007
 * @date 2024/3/15
 */
@Introspected
@Serdeable(naming = SnakeCaseStrategy.class)
@Data
public class SysConfigDTO {
    /**
     * 密码是否加密传输
     */
    private boolean passwordEncrypt;

    /**
     * 密钥
     */
    private String publicKey;

    public static SysConfigDTO convert(SysConfig sysConfig) {
        SysConfigDTO sysConfigDTO = new SysConfigDTO();
        MortnonBeanUtils.copy(sysConfig, sysConfigDTO);
        return sysConfigDTO;
    }
}

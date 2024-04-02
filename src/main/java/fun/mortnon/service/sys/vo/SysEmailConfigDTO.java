package fun.mortnon.service.sys.vo;

import fun.mortnon.dal.sys.entity.message.SmtpSecurity;
import fun.mortnon.dal.sys.entity.message.SysEmailConfig;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

/**
 * @author dev2007
 * @date 2024/3/29
 */
@Introspected
@Serdeable(naming = SnakeCaseStrategy.class)
@Data
public class SysEmailConfigDTO {
    private String host;
    private String port;
    private boolean enabled;
    private Long connectionTimeout;
    private Long timeout;
    private SmtpSecurity https;
    private boolean auth;
    private String email;
    private String userName;

    public static SysEmailConfigDTO convert(SysEmailConfig sysEmailConfig) {
        SysEmailConfigDTO sysEmailConfigDTO = new SysEmailConfigDTO();

        if (!sysEmailConfig.isEnabled()) {
            sysEmailConfigDTO.setEnabled(false);
            return sysEmailConfigDTO;
        }

        sysEmailConfigDTO.setHost(sysEmailConfig.getHost());
        sysEmailConfigDTO.setPort(sysEmailConfig.getPort());
        sysEmailConfigDTO.setEnabled(sysEmailConfig.isEnabled());
        sysEmailConfigDTO.setConnectionTimeout(sysEmailConfig.getConnectionTimeout());
        sysEmailConfigDTO.setTimeout(sysEmailConfig.getTimeout());
        sysEmailConfigDTO.setHttps(sysEmailConfig.getHttps());
        sysEmailConfigDTO.setAuth(sysEmailConfig.isAuth());
        sysEmailConfigDTO.setEmail(sysEmailConfig.getEmail());
        sysEmailConfigDTO.setUserName(sysEmailConfig.getUserName());

        return sysEmailConfigDTO;
    }
}

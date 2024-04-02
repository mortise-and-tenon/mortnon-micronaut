package fun.mortnon.dal.sys.entity.message;

import fun.mortnon.dal.base.entity.BaseEntity;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

/**
 * @author dev2007
 * @date 2024/3/21
 */
@Serdeable
@MappedEntity
@Data
public class SysEmailConfig extends BaseEntity {
    private String host;
    private String port;
    private boolean enabled;
    private boolean debug;
    private Long connectionTimeout;
    private Long timeout;
    private SmtpSecurity https;
    private boolean auth;
    private String email;
    private String userName;
    private String password;
}

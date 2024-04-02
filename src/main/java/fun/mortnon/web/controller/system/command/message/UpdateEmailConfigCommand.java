package fun.mortnon.web.controller.system.command.message;

import fun.mortnon.dal.sys.entity.message.SmtpSecurity;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

/**
 * @author dev2007
 * @date 2024/3/29
 */
@Introspected
@Serdeable(naming = SnakeCaseStrategy.class)
@Data
public class UpdateEmailConfigCommand {
    /**
     * 邮箱服务器
     */
    @NotEmpty
    private String host;

    /**
     * SMTP 协议端口
     */
    @Positive
    private Integer port;

    /**
     * 是否启用配置
     */
    private boolean enabled;

    /**
     * 连接超时
     */
    private Long connectionTimeout;

    /**
     * 发送超时
     */
    private Long timeout;

    /**
     * 安全协议
     */
    private SmtpSecurity https;

    /**
     * 是否有认证
     */
    private boolean auth;

    /**
     * 消息中心邮箱地址
     */
    private String email;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码或认证码
     */
    private String password;
}

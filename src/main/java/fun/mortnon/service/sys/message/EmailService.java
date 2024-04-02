package fun.mortnon.service.sys.message;

import fun.mortnon.service.sys.vo.SysEmailConfigDTO;
import fun.mortnon.web.controller.system.command.message.TestEmailConfigCommand;
import fun.mortnon.web.controller.system.command.message.UpdateEmailConfigCommand;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * @author dev2007
 * @date 2024/3/21
 */
public interface EmailService {
    /**
     * 查询配置
     *
     * @return
     */

    Mono<SysEmailConfigDTO> queryConfig();

    /**
     * 保存邮箱配置
     *
     * @param configCommand
     * @return
     */
    Mono<SysEmailConfigDTO> saveEmailConfiguration(UpdateEmailConfigCommand configCommand);

    /**
     * 发送邮件
     *
     * @param toUsers
     * @param templateName
     * @param parameters
     */
    void sendEmail(List<Long> toUsers, String templateName, Map<String, Object> parameters);

    /**
     * 向邮箱配置邮箱发送验证码
     *
     * @param testEmailConfigCommand
     * @return
     */
    boolean sendTestEmail(TestEmailConfigCommand testEmailConfigCommand);
}

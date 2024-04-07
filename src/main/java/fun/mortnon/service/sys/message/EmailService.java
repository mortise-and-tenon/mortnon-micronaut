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
     * 向指定用户发送邮件
     *
     * @param toUsers
     * @param templateName
     * @param parameters
     */
    void sendEmailToUser(List<Long> toUsers, String templateName, Map<String, Object> parameters);

    /**
     * 向指定邮箱发送邮件
     *
     * @param toEmails
     * @param templateName
     * @param parameters
     */
    void sendEmailToInbox(List<String> toEmails, String templateName, Map<String, Object> parameters);

    /**
     * 向邮箱配置邮箱发送验证码
     *
     * @param testEmailConfigCommand
     * @param userName               当前登录用户的用户名
     * @return
     */
    Mono<Boolean> sendTestEmail(TestEmailConfigCommand testEmailConfigCommand, String userName);
}

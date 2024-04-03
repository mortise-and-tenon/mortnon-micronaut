package fun.mortnon.web.controller.system;

import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.service.sys.message.EmailService;
import fun.mortnon.service.sys.vo.SysEmailConfigDTO;
import fun.mortnon.web.controller.system.command.message.TestEmailConfigCommand;
import fun.mortnon.web.controller.system.command.message.UpdateEmailConfigCommand;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Put;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

import java.security.Principal;

/**
 * @author dev2007
 * @date 2024/3/21
 */
@Controller("/system/message")
public class MessageController {
    @Inject
    private EmailService emailService;

    /**
     * 查询邮箱配置
     *
     * @return
     */
    @Get("/email")
    public Mono<MortnonResult<SysEmailConfigDTO>> queryEmailConfig() {
        return emailService.queryConfig().map(MortnonResult::success);
    }

    /**
     * 修改邮箱配置
     *
     * @param emailConfigCommand
     * @return
     */
    @Put("/email")
    public Mono<MutableHttpResponse<MortnonResult<SysEmailConfigDTO>>> updateEmailConfig(@Body UpdateEmailConfigCommand emailConfigCommand) {
        return emailService.saveEmailConfiguration(emailConfigCommand)
                .map(MortnonResult::success)
                .map(HttpResponse::ok);
    }

    /**
     * 向指定邮箱配置发送验证码
     *
     * @param testEmailConfigCommand
     * @return
     */
    @Put("/email/code")
    public Mono<MutableHttpResponse<MortnonResult<SysEmailConfigDTO>>> sendEmailCode(@Body TestEmailConfigCommand testEmailConfigCommand,
                                                                                     Principal principal) {
        return emailService.sendTestEmail(testEmailConfigCommand,principal.getName())
                .map(MortnonResult::success)
                .map(HttpResponse::ok);
    }
}

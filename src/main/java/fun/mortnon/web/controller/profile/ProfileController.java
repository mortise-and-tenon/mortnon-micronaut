package fun.mortnon.web.controller.profile;

import fun.mortnon.framework.aop.OperationLog;
import fun.mortnon.framework.constants.LogConstants;
import fun.mortnon.framework.exceptions.ParameterException;
import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.service.sys.ProfileService;
import fun.mortnon.service.sys.SysMenuService;
import fun.mortnon.service.sys.SysUserService;
import fun.mortnon.service.sys.vo.ProfileDTO;
import fun.mortnon.service.sys.vo.SysMenuTreeDTO;
import fun.mortnon.service.sys.vo.SysUserDTO;
import fun.mortnon.web.controller.user.command.UpdatePasswordCommand;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.security.Principal;
import java.util.List;


/**
 * 用户个人相关
 *
 * @author dev2007
 * @date 2024/2/27
 */
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/profile")
public class ProfileController {

    /**
     * 用户 Profile 服务
     */
    @Inject
    private ProfileService profileService;

    /**
     * 用户服务
     */
    @Inject
    private SysUserService sysUserService;

    /**
     * 查询当前登录用户信息
     *
     * @param principal
     * @return
     */
    @Get
    public Mono<MortnonResult<ProfileDTO>> queryUser(@Nullable Principal principal) {
        return profileService.queryProfile(principal)
                .map(MortnonResult::success);
    }

    /**
     * 修改当前登录用户密码
     *
     * @param authentication
     * @param updatePasswordCommand
     * @return
     */
    @OperationLog(LogConstants.USER_PASSWORD_UPDATE)
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Put("/password")
    public Mono<MutableHttpResponse<MortnonResult>> updateUserPassword(Authentication authentication, @Body @NotNull UpdatePasswordCommand updatePasswordCommand) {
        if (null == authentication) {
            return Mono.error(ParameterException.create("auth is null."));
        }

        String userName = authentication.getName();
        updatePasswordCommand.setId(0L);
        updatePasswordCommand.setUserName(userName);
        return sysUserService.updateSelfPassword(updatePasswordCommand).map(MortnonResult::success).map(HttpResponse::ok);
    }
}

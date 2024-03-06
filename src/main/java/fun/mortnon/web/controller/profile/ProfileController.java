package fun.mortnon.web.controller.profile;

import fun.mortnon.framework.aop.OperationLog;
import fun.mortnon.framework.constants.LogConstants;
import fun.mortnon.framework.exceptions.ParameterException;
import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.service.sys.SysMenuService;
import fun.mortnon.service.sys.SysUserService;
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

    @Inject
    private SysUserService sysUserService;

    @Inject
    private SysMenuService sysMenuService;

    /**
     * 查询当前登录用户信息
     *
     * @param principal
     * @return
     */
    @Get("/user")
    public Mono<MortnonResult<SysUserDTO>> queryUser(@Nullable Principal principal) {
        return sysUserService.getUserByUsername(principal.getName())
                .map(SysUserDTO::convert)
                .map(MortnonResult::success);
    }

    /**
     * 查询当前登录用户菜单
     *
     * @param principal
     * @return
     */
    @Get("/menus")
    public Mono<MortnonResult<List<SysMenuTreeDTO>>> queryMenuTree(@Nullable Principal principal) {
        return sysMenuService.queryUserMenu(principal).map(MortnonResult::success);
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

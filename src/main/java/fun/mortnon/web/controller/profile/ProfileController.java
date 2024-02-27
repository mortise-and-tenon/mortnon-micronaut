package fun.mortnon.web.controller.profile;

import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.service.sys.SysMenuService;
import fun.mortnon.service.sys.SysUserService;
import fun.mortnon.service.sys.vo.SysMenuDTO;
import fun.mortnon.service.sys.vo.SysUserDTO;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.List;


/**
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
     * 查询已登录用户自身信息
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

    @Get("/menus")
    public Mono<MortnonResult<List<SysMenuDTO>>> queryMenuTree(@Nullable Principal principal) {
        return sysMenuService.queryUserMenu(principal).map(MortnonResult::success);
    }
}

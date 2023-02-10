package fun.mortnon.web.controller.user;

import fun.mortnon.dal.sys.entity.SysUser;
import fun.mortnon.framework.enums.ErrorCodeEnum;
import fun.mortnon.framework.enums.Sex;
import fun.mortnon.framework.exceptions.MortnonBaseException;
import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.framework.vo.PageableData;
import fun.mortnon.service.sys.SysUserService;
import fun.mortnon.service.sys.vo.SysUserDTO;
import fun.mortnon.web.controller.user.command.CreateUserCommand;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;

/**
 * @author dev2007
 * @date 2023/2/7
 */
//@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/users")
public class UserController {
    @Inject
    private SysUserService sysUserService;

    @Get
    public Mono<MortnonResult<PageableData<List<SysUserDTO>>>> queryUser(@Valid Pageable pageable) {
        return sysUserService.queryUsers(pageable).map(MortnonResult::success);
    }

    @Post
    public Mono<MutableHttpResponse<MortnonResult>> save(@NonNull CreateUserCommand createUserCommand) {
        return sysUserService.createUser(createUserCommand).map(data -> MortnonResult.success(data))
                .map(HttpResponse::created)
                .onErrorReturn(HttpResponse.badRequest(MortnonResult.fail(ErrorCodeEnum.PARAM_ERROR)));
    }

    @Get("/{name}")
    public Mono<SysUser> queryUser(@NotBlank String name) {
        return sysUserService.getUserByUsername(name);
    }
}

package fun.mortnon.web.controller.user;

import fun.mortnon.dal.sys.entity.SysUser;
import fun.mortnon.framework.enums.ErrorCodeEnum;
import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.framework.vo.PageableData;
import fun.mortnon.service.sys.SysUserService;
import fun.mortnon.service.sys.vo.SysUserDTO;
import fun.mortnon.web.controller.user.command.CreateUserCommand;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author dev2007
 * @date 2023/2/7
 */
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/users")
@Slf4j
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
                .onErrorResume(e ->
                        Mono.fromCallable(() -> HttpResponse.badRequest(MortnonResult.fail(ErrorCodeEnum.PARAM_ERROR, e.getMessage())))
                )
                .onErrorReturn(HttpResponse.badRequest(MortnonResult.fail(ErrorCodeEnum.PARAM_ERROR)));
    }

    @Delete("/{name}")
    public Mono<MutableHttpResponse<MortnonResult>> delete(@NonNull String name) {
        return sysUserService.deleteUser(name)
                .map(result -> HttpResponse.ok(MortnonResult.success(result)))
                .onErrorResume(e -> Mono.fromCallable(() -> HttpResponse.badRequest(MortnonResult.fail(ErrorCodeEnum.USER_ERROR))));
    }

    @Get("/{name}")
    public Mono<MutableHttpResponse<MortnonResult>> queryUser(@NotBlank String name) {
        return sysUserService.getUserByUsername(name).map(SysUserDTO::convert)
                .map(result -> HttpResponse.ok(MortnonResult.success(result)))
                .onErrorResume(e -> Mono.just(HttpResponse.badRequest(MortnonResult.fail(ErrorCodeEnum.PARAM_ERROR, e.getMessage()))))
                .onErrorReturn(HttpResponse.badRequest(MortnonResult.fail(ErrorCodeEnum.PARAM_ERROR)));
    }
}

package fun.mortnon.web.controller.user;

import fun.mortnon.framework.enums.ErrorCodeEnum;
import fun.mortnon.framework.exceptions.MortnonBaseException;
import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.framework.vo.PageableData;
import fun.mortnon.service.sys.SysUserService;
import fun.mortnon.service.sys.vo.SysUserDTO;
import fun.mortnon.web.controller.user.command.CreateUserCommand;
import fun.mortnon.web.controller.user.command.UpdateUserCommand;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.*;
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

    /**
     * 查询用户列表
     *
     * @param pageable
     * @return
     */
    @Get
    public Mono<MortnonResult<PageableData<List<SysUserDTO>>>> queryUser(@Valid Pageable pageable) {
        return sysUserService.queryUsers(pageable).map(MortnonResult::successPageData);
    }

    /**
     * 创建用户
     *
     * @param createUserCommand 用户数据
     * @return
     */
    @Post
    public Mono<MutableHttpResponse<MortnonResult>> save(@NonNull CreateUserCommand createUserCommand) {
        return sysUserService.createUser(createUserCommand).map(data -> MortnonResult.success(data))
                .map(HttpResponse::created)
                .onErrorReturn(HttpResponse.badRequest(MortnonResult.fail(ErrorCodeEnum.PARAM_ERROR)));
    }

    /**
     * 修改用户信息
     *
     * @param updateUserCommand
     * @return
     */
    @Put
    public Mono<MutableHttpResponse<MortnonResult>> update(@NonNull UpdateUserCommand updateUserCommand) {
        return sysUserService.updateUser(updateUserCommand)
                .map(SysUserDTO::convert)
                .map(MortnonResult::success)
                .map(HttpResponse::ok)
                .onErrorReturn(HttpResponse.badRequest(MortnonResult.fail(ErrorCodeEnum.PARAM_ERROR)));
    }

    /**
     * 删除指定用户
     *
     * @param name 用户名
     * @return
     */
    @Delete("/{name}")
    public Mono<MutableHttpResponse<MortnonResult>> delete(@NonNull String name) {
        return sysUserService.deleteUser(name)
                .map(MortnonResult::success)
                .map(HttpResponse::ok);
    }

    /**
     * 获取指定用户
     *
     * @param name 用户名
     * @return
     */
    @Get("/{name}")
    public Mono<MutableHttpResponse<MortnonResult>> queryUser(@NotBlank String name) {
        return sysUserService.getUserByUsername(name).map(SysUserDTO::convert)
                .map(MortnonResult::success)
                .map(HttpResponse::ok)
                .onErrorReturn(HttpResponse.badRequest(MortnonResult.fail(ErrorCodeEnum.PARAM_ERROR)));
    }
}

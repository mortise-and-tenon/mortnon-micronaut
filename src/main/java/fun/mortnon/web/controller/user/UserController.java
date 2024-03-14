package fun.mortnon.web.controller.user;

import fun.mortnon.framework.aop.OperationLog;
import fun.mortnon.framework.constants.LogConstants;
import fun.mortnon.framework.enums.ErrorCodeEnum;
import fun.mortnon.framework.exceptions.ParameterException;
import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.framework.vo.PageableData;
import fun.mortnon.framework.vo.PageableQuery;
import fun.mortnon.service.sys.SysUserService;
import fun.mortnon.service.sys.vo.SysUserDTO;
import fun.mortnon.web.controller.user.command.CreateUserCommand;
import fun.mortnon.web.controller.user.command.UpdateUserCommand;
import fun.mortnon.web.controller.user.command.UpdatePasswordCommand;
import fun.mortnon.web.controller.user.command.UpdateUserStatusCommand;
import fun.mortnon.web.controller.user.command.UserPageSearch;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.http.multipart.StreamingFileUpload;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.security.Principal;
import java.util.List;

import static io.micronaut.http.MediaType.MULTIPART_FORM_DATA;

/**
 * 用户
 *
 * @author dev2007
 * @date 2023/2/7
 */
@Controller("/users")
@Slf4j
public class UserController {
    /**
     * 用户服务
     */
    @Inject
    private SysUserService sysUserService;

    /**
     * 查询用户
     *
     * @param pageSearch 查询参数、分页、排序
     * @return
     */
    @Get("{?pageSearch*}")
    public Mono<MortnonResult<PageableData<List<SysUserDTO>>>> queryUser(UserPageSearch pageSearch) {
        return sysUserService.queryUsers(pageSearch)
                .map(MortnonResult::successPageData);
    }

    /**
     * 创建用户
     *
     * @param createUserCommand 用户数据
     * @return
     */
    @OperationLog(LogConstants.USER_CREATE)
    @Post
    public Mono<MutableHttpResponse<MortnonResult>> save(@NonNull @Valid CreateUserCommand createUserCommand) {
        return sysUserService.createUser(createUserCommand)
                .map(data -> MortnonResult.success(data))
                .map(HttpResponse::created);
    }

    /**
     * 修改用户信息
     *
     * @param updateUserCommand
     * @return
     */
    @OperationLog(LogConstants.USER_UPDATE)
    @Put
    public Mono<MutableHttpResponse<MortnonResult>> update(@NonNull @Valid UpdateUserCommand updateUserCommand) {
        return sysUserService.updateUser(updateUserCommand)
                .map(MortnonResult::success)
                .map(HttpResponse::ok);
    }

    /**
     * 删除指定用户
     *
     * @param id 用户 id
     * @return
     */
    @OperationLog(LogConstants.USER_DELETE)
    @Delete("/{id}")
    public Mono<MutableHttpResponse<MortnonResult>> delete(@NotNull @Positive Long id) {
        return sysUserService.deleteUser(id)
                .map(MortnonResult::success)
                .map(HttpResponse::ok);
    }

    @OperationLog(LogConstants.USER_DELETE)
    @Delete("/batch/{ids}")
    public Mono<MutableHttpResponse<MortnonResult>> deleteBatch(@NotNull String ids) {
        return sysUserService.batchDeleteUser(ids)
                .map(MortnonResult::success)
                .map(HttpResponse::ok);
    }

    /**
     * 获取指定用户
     *
     * @param id 用户名 id
     * @return
     */
    @Get("/{id}")
    public Mono<MutableHttpResponse<MortnonResult>> queryUser(@NotNull @Positive Long id) {
        return sysUserService.getUserById(id)
                .map(MortnonResult::success)
                .map(HttpResponse::ok);
    }

    /**
     * 变更用户状态
     *
     * @param update
     * @return
     */
    @OperationLog(LogConstants.USER_STATUS_UPDATE)
    @Put("/status")
    public Mono<MutableHttpResponse<MortnonResult>> changeStatus(@Body UpdateUserStatusCommand update) {
        return sysUserService.updateUserStatus(update)
                .map(MortnonResult::success)
                .map(HttpResponse::ok);
    }

    /**
     * 修改其他用户密码
     *
     * @param id
     * @param updatePassword
     * @return
     */
    @OperationLog(LogConstants.USER_PASSWORD_UPDATE)
    @Put("/password/{id}")
    public Mono<MutableHttpResponse<MortnonResult>> updateUserPassword(@NotNull @Positive Long id, @Body @NotNull @Valid UpdatePasswordCommand updatePassword) {
        updatePassword.setId(id);
        return sysUserService.updateUserPassword(updatePassword)
                .map(MortnonResult::success)
                .map(HttpResponse::ok);
    }

    /**
     * 导入用户
     *
     * @param file
     * @return
     */
    @OperationLog(LogConstants.USER_IMPORT)
    @Post(value = "/import", consumes = MULTIPART_FORM_DATA)
    public Mono<MutableHttpResponse<MortnonResult>> importUser(StreamingFileUpload file) {
        return sysUserService.importUser(file)
                .map(MortnonResult::success)
                .map(HttpResponse::ok);
    }
}

package fun.mortnon.web.controller.profile;

import fun.mortnon.framework.aop.OperationLog;
import fun.mortnon.framework.constants.LogConstants;
import fun.mortnon.framework.enums.ErrorCodeEnum;
import fun.mortnon.framework.exceptions.ParameterException;
import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.service.sys.ProfileService;
import fun.mortnon.service.sys.SysMenuService;
import fun.mortnon.service.sys.SysUserService;
import fun.mortnon.service.sys.vo.ProfileDTO;
import fun.mortnon.service.sys.vo.SysMenuTreeDTO;
import fun.mortnon.service.sys.vo.SysUserDTO;
import fun.mortnon.web.controller.user.command.UpdatePasswordCommand;
import fun.mortnon.web.controller.user.command.UpdateUserCommand;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Patch;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.multipart.StreamingFileUpload;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

import static io.micronaut.http.MediaType.MULTIPART_FORM_DATA;
import static io.micronaut.http.MediaType.TEXT_PLAIN;


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
     * 更新用户基本信息
     *
     * @param principal
     * @param updateUserCommand
     * @return
     */
    @OperationLog(LogConstants.USER_UPDATE)
    @Patch
    public Mono<MutableHttpResponse<MortnonResult<SysUserDTO>>> updateUser(@Nullable Principal principal,
                                                                           @Body UpdateUserCommand updateUserCommand) {
        return profileService.updateProfile(principal, updateUserCommand)
                .map(MortnonResult::success)
                .map(HttpResponse::ok);
    }

    /**
     * 修改当前登录用户密码
     *
     * @param authentication
     * @param updatePasswordCommand
     * @return
     */
    @OperationLog(LogConstants.USER_PASSWORD_UPDATE)
    @Put("/password")
    public Mono<MutableHttpResponse<MortnonResult>> updateUserPassword(Authentication authentication,
                                                                       @Body @NotNull UpdatePasswordCommand updatePasswordCommand) {
        if (null == authentication) {
            return Mono.error(ParameterException.create(ErrorCodeEnum.AUTH_EMPTY));
        }

        String userName = authentication.getName();
        updatePasswordCommand.setId(0L);
        updatePasswordCommand.setUserName(userName);
        return sysUserService.updateSelfPassword(updatePasswordCommand).map(MortnonResult::success).map(HttpResponse::ok);
    }

    /**
     * 上传头像
     *
     * @param file
     * @return
     */
    @Post(value = "/avatar", consumes = MULTIPART_FORM_DATA)
    public Mono<MutableHttpResponse<MortnonResult>> upload(@Nullable Principal principal, StreamingFileUpload file) {
        return profileService.uploadAvatar(principal, file)
                .map(path -> {
                    if (StringUtils.isNotEmpty(path)) {
                        return HttpResponse.ok(MortnonResult.success().setData(path));
                    } else {
                        return HttpResponse.ok(MortnonResult.fail(ErrorCodeEnum.UPLOAD_FAIL));
                    }
                });

    }
}

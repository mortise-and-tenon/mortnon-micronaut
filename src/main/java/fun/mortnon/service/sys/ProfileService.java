package fun.mortnon.service.sys;

import fun.mortnon.service.sys.vo.ProfileDTO;
import fun.mortnon.service.sys.vo.SysUserDTO;
import fun.mortnon.web.controller.user.command.UpdateUserCommand;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.multipart.StreamingFileUpload;
import reactor.core.publisher.Mono;

import java.security.Principal;

/**
 * Profile 服务
 *
 * @author dev2007
 * @date 2024/3/11
 */
public interface ProfileService {
    /**
     * 查询当前用户信息
     *
     * @param principal
     * @return
     */
    Mono<ProfileDTO> queryProfile(@Nullable Principal principal);

    /**
     * 更新当前用户信息
     *
     * @param principal
     * @param updateUserCommand
     * @return
     */
    Mono<SysUserDTO> updateProfile(@Nullable Principal principal, UpdateUserCommand updateUserCommand);

    /**
     * 上传头像
     *
     * @param principal
     * @param file
     * @return
     */
    Mono<String> uploadAvatar(Principal principal, StreamingFileUpload file);
}

package fun.mortnon.service.sys;

import fun.mortnon.dal.sys.entity.SysProject;
import fun.mortnon.dal.sys.entity.SysRole;
import fun.mortnon.dal.sys.entity.SysUser;
import fun.mortnon.service.sys.vo.SysUserDTO;
import fun.mortnon.web.controller.user.command.CreateUserCommand;
import fun.mortnon.web.controller.user.command.UpdateUserCommand;
import fun.mortnon.web.controller.user.command.UpdatePasswordCommand;
import fun.mortnon.web.controller.user.command.UpdateUserStatusCommand;
import fun.mortnon.web.controller.user.command.UserPageSearch;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.multipart.StreamingFileUpload;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

/**
 * @author dongfangzan
 * @date 28.4.21 3:52 下午
 */
public interface SysUserService {

    /**
     * 保存用户
     *
     * @param createUserCommand
     * @return
     */
    Mono<SysUserDTO> createUser(CreateUserCommand createUserCommand);

    /**
     * 查询指定用户
     *
     * @param id 用户 id
     * @return
     */
    Mono<SysUserDTO> getUserById(Long id);

    /**
     * 查询用户
     *
     * @param pageSearch 查询参数、分页、排序
     * @return
     */
    Mono<Page<SysUserDTO>> queryUsers(UserPageSearch pageSearch);

    /**
     * 按用户名查询用户
     *
     * @param userName
     * @return
     */
    Mono<SysUser> getUserByUsername(String userName);

    /**
     * 删除指定用户
     *
     * @param userId
     * @return
     */
    Mono<Boolean> deleteUser(Long userId);

    /**
     * 批量删除用户
     *
     * @param ids
     * @return
     */
    Mono<Boolean> batchDeleteUser(String ids);

    /**
     * 更新用户
     *
     * @param updateUserCommand
     * @return
     */
    Mono<SysUserDTO> updateUser(UpdateUserCommand updateUserCommand);

    /**
     * 查询用户对应角色
     *
     * @param userName
     * @return
     */
    Flux<SysRole> queryUserRole(String userName);

    /**
     * 查询用户对应的组织
     *
     * @param userName
     * @return
     */
    Flux<SysProject> queryUserProject(String userName);

    /**
     * 修改用户密码
     *
     * @param updatePasswordCommand
     * @return
     */
    Mono<Boolean> updateUserPassword(UpdatePasswordCommand updatePasswordCommand);

    /**
     * 变更用户状态
     *
     * @param updateUserStatusCommand
     * @return
     */
    Mono<Boolean> updateUserStatus(UpdateUserStatusCommand updateUserStatusCommand);

    /**
     * 修改用户自身密码
     *
     * @param updatePasswordCommand
     * @return
     */
    Mono<Boolean> updateSelfPassword(UpdatePasswordCommand updatePasswordCommand);

    /**
     * 导入用户
     *
     * @param file
     * @return
     */
    Mono<List<SysUserDTO>> importUser(StreamingFileUpload file);
}

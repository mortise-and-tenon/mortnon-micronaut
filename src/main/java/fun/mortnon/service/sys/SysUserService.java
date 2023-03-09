package fun.mortnon.service.sys;

import fun.mortnon.dal.sys.entity.SysProject;
import fun.mortnon.dal.sys.entity.SysRole;
import fun.mortnon.dal.sys.entity.SysUser;
import fun.mortnon.service.sys.vo.SysUserDTO;
import fun.mortnon.web.controller.user.command.CreateUserCommand;
import fun.mortnon.web.controller.user.command.UpdateUserCommand;
import fun.mortnon.web.controller.user.command.UpdatePasswordCommand;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
     * 根据账户名获取用户信息
     *
     * @param id 用户 id
     * @return 用户信息
     */
    Mono<SysUser> getUserById(Long id);

    /**
     * 查询用户列表
     *
     * @param pageable 分页数据
     * @return
     */
    Mono<Page<SysUserDTO>> queryUsers(Pageable pageable);

    /**
     * 按用户名查询用户
     *
     * @param userName
     * @return
     */
    Mono<SysUser> getUserByUsername(String userName);

    /**
     * 删除用户
     *
     * @param userId
     * @return
     */
    Mono<Boolean> deleteUser(Long userId);

    /**
     * 更新用户
     *
     * @param updateUserCommand
     * @return
     */
    Mono<SysUser> updateUser(UpdateUserCommand updateUserCommand);

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
     * 修改用户自身密码
     *
     * @param updatePasswordCommand
     * @return
     */
    Mono<Boolean> updateSelfPassword(UpdatePasswordCommand updatePasswordCommand);
}

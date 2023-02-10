package fun.mortnon.service.sys;

import fun.mortnon.dal.sys.entity.SysRole;
import fun.mortnon.dal.sys.entity.SysUser;
import fun.mortnon.service.sys.vo.SysUserDTO;
import fun.mortnon.web.controller.user.command.CreateUserCommand;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
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
     * @param username 账户名
     * @return 用户信息
     */
    Mono<SysUser> getUserByUsername(String username);

    /**
     * 查询用户列表
     *
     * @param pageable 分页数据
     * @return
     */
    Mono<Page<SysUserDTO>> queryUsers(Pageable pageable);

    /**
     * 查询用户对应角色
     *
     * @param userName
     * @return
     */
    Mono<SysRole> queryUserRole(String userName);
}

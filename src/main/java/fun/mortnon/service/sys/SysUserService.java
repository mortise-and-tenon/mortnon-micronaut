package fun.mortnon.service.sys;

import fun.mortnon.dal.sys.domain.SysUser;
import fun.mortnon.web.entity.SysUserVo;
import reactor.core.publisher.Mono;

/**
 * @author dongfangzan
 * @date 28.4.21 3:52 下午
 */
public interface SysUserService {

    Mono<SysUser> createUser(SysUserVo sysUserVo);

    /**
     * 根据账户名获取用户信息
     *
     * @param username 账户名
     * @return 用户信息
     */
    SysUser getUserByUsername(String username);
}

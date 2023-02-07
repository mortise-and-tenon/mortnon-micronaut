package fun.mortnon.service.sys.impl;

import fun.mortnon.dal.sys.domain.SysUser;
import fun.mortnon.dal.sys.repository.UserRepository;
import fun.mortnon.service.sys.SysUserService;
import fun.mortnon.web.entity.SysUserVo;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Date;


/**
 * @author dongfangzan
 * @date 28.4.21 3:53 下午
 */
@Singleton
public class SysUserServiceImpl implements SysUserService {

    /**
     * sysUser 仓储
     */
    @Inject
    private UserRepository sysUserMapper;

    @Override
    public Mono<SysUser> createUser(SysUserVo sysUserVo) {
        SysUser sysUser = new SysUser();
        sysUser.setUserName(sysUserVo.getUserName());
        sysUser.setNickName(sysUserVo.getNickName());
        sysUser.setPassword(sysUserVo.getPassword());
        sysUser.setSalt(sysUserVo.getSalt());
        sysUser.setSex(sysUserVo.getSex());
        return sysUserMapper.save(sysUser);
    }

    @Override
    public SysUser getUserByUsername(String username) {
        if (StringUtils.isBlank(username)) {
            return null;
        }

        return sysUserMapper.findByUserName(username).block();
    }
}

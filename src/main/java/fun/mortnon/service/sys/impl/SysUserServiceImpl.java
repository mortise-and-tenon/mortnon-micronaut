package fun.mortnon.service.sys.impl;

import fun.mortnon.dal.sys.entity.SysRole;
import fun.mortnon.dal.sys.entity.SysUser;
import fun.mortnon.dal.sys.repository.RoleRepository;
import fun.mortnon.dal.sys.repository.UserRepository;
import fun.mortnon.dal.sys.repository.UserRoleRepository;
import fun.mortnon.framework.enums.ErrorCodeEnum;
import fun.mortnon.framework.exceptions.MortnonBaseException;
import fun.mortnon.service.sys.SysUserService;
import fun.mortnon.service.sys.vo.SysUserDTO;
import fun.mortnon.web.controller.user.command.CreateUserCommand;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


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

    @Inject
    private UserRoleRepository userRoleRepository;

    @Inject
    private RoleRepository roleRepository;

    @Override
    public Mono<SysUserDTO> createUser(CreateUserCommand createUserCommand) {
        if (StringUtils.isEmpty(createUserCommand.getPassword()) || StringUtils.isEmpty(createUserCommand.getRepeatPassword())) {
            return Mono.error(new MortnonBaseException(ErrorCodeEnum.PARAM_ERROR, "password is empty"));
        }

        if (!createUserCommand.getPassword().equals(createUserCommand.getRepeatPassword())) {
            return Mono.error(new MortnonBaseException(ErrorCodeEnum.PARAM_ERROR, "password is not match"));
        }

        SysUser sysUser = new SysUser();
        sysUser.setUserName(createUserCommand.getUserName());
        sysUser.setNickName(createUserCommand.getNickName());
        sysUser.setSalt(RandomStringUtils.randomAlphanumeric(6));
        String password = createUserCommand.getPassword();
        password = DigestUtils.sha256Hex(password + sysUser.getSalt());
        sysUser.setPassword(password);
        sysUser.setSex(createUserCommand.getSex());
        Date now = new Date();
        sysUser.setGmtCreate(now);
        sysUser.setGmtModify(now);
        return sysUserMapper.save(sysUser).map(SysUserDTO::convert);
    }

    @Override
    public Mono<SysUser> getUserByUsername(String username) {
        if (StringUtils.isBlank(username)) {
            return Mono.empty();
        }

        return sysUserMapper.findByUserName(username);
    }

    @Override
    public Mono<Page<SysUserDTO>> queryUsers(Pageable pageable) {
        return sysUserMapper.findAll(pageable).map(k -> {
            List<SysUserDTO> collect = k.getContent().stream().map(data -> SysUserDTO.convert(data)).collect(Collectors.toList());
            return Page.of(collect, k.getPageable(), k.getTotalSize());
        });
    }

    @Override
    public Mono<SysRole> queryUserRole(String userName) {
        return getUserByUsername(userName).flatMap(user -> userRoleRepository.findByUserId(user.getId()))
                .flatMap(userRole -> roleRepository.findById(userRole.getRoleId()));
    }
}

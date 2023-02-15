package fun.mortnon.service.sys.impl;

import fun.mortnon.dal.sys.entity.SysRole;
import fun.mortnon.dal.sys.entity.SysUser;
import fun.mortnon.dal.sys.repository.RoleRepository;
import fun.mortnon.dal.sys.repository.UserRepository;
import fun.mortnon.dal.sys.repository.AssignmentRepository;
import fun.mortnon.framework.enums.ErrorCodeEnum;
import fun.mortnon.framework.exceptions.MortnonBaseException;
import fun.mortnon.service.sys.SysUserService;
import fun.mortnon.service.sys.vo.SysUserDTO;
import fun.mortnon.web.controller.user.command.CreateUserCommand;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 用户服务
 *
 * @author dongfangzan
 * @date 28.4.21 3:53 下午
 */
@Singleton
@Slf4j
public class SysUserServiceImpl implements SysUserService {

    /**
     * sysUser 仓储
     */
    @Inject
    private UserRepository sysUserMapper;

    @Inject
    private AssignmentRepository assignmentRepository;

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

        if (StringUtils.isEmpty(createUserCommand.getUserName())) {
            return Mono.error(new MortnonBaseException(ErrorCodeEnum.PARAM_ERROR, "user name is empty"));
        }

        if (StringUtils.isEmpty(createUserCommand.getNickName())) {
            return Mono.error(new MortnonBaseException(ErrorCodeEnum.PARAM_ERROR, "nick name is empty"));
        }

        return sysUserMapper.findByUserName(createUserCommand.getUserName())
                .flatMap(t -> {
                    if (null != t) {
                        log.info("repeat user name:{}", createUserCommand.getUserName());
                        return Mono.error(new MortnonBaseException(ErrorCodeEnum.USERNAME_ALREADY_EXISTS, "user name is repeat."));
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
                });
    }

    @Override
    public Mono<SysUser> getUserByUsername(String username) {
        log.info("query username:{}", username);
        return sysUserMapper.existsByUserName(username)
                .flatMap(result -> {
                    if (!result) {
                        log.info("user name [{}] is not exists.", username);
                        return Mono.error(new MortnonBaseException(ErrorCodeEnum.PARAM_ERROR, "user not exists."));
                    }
                    return sysUserMapper.findByUserName(username);
                });
    }

    @Override
    public Mono<Page<SysUserDTO>> queryUsers(Pageable pageable) {
        return sysUserMapper.findAll(pageable).map(k -> {
            List<SysUserDTO> collect = k.getContent().stream().map(data -> SysUserDTO.convert(data)).collect(Collectors.toList());
            return Page.of(collect, k.getPageable(), k.getTotalSize());
        });
    }

    @Override
    public Mono<Boolean> deleteUser(String userName) {
        return sysUserMapper.existsByUserName(userName)
                .flatMap(result -> {
                    log.info("query user by name:{}", userName);
                    if (!result) {
                        log.info("user name {} is not exists,return default true.");
                        return Mono.just(true);
                    }
                    return sysUserMapper.deleteByUserName(userName);
                }).map(t -> true);
    }

    @Override
    public Mono<SysRole> queryUserRole(String userName) {
        return getUserByUsername(userName).flatMap(user -> assignmentRepository.findByUserId(user.getId()))
                .flatMap(userRole -> roleRepository.findById(userRole.getRoleId()));
    }
}

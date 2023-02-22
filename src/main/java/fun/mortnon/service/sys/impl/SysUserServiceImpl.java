package fun.mortnon.service.sys.impl;

import fun.mortnon.dal.sys.entity.SysRole;
import fun.mortnon.dal.sys.entity.SysUser;
import fun.mortnon.dal.sys.repository.AssignmentRepository;
import fun.mortnon.dal.sys.repository.RoleRepository;
import fun.mortnon.dal.sys.repository.UserRepository;
import fun.mortnon.framework.enums.ErrorCodeEnum;
import fun.mortnon.framework.exceptions.MortnonBaseException;
import fun.mortnon.framework.exceptions.NotFoundException;
import fun.mortnon.framework.exceptions.ParameterException;
import fun.mortnon.framework.exceptions.RepeatDataException;
import fun.mortnon.service.sys.SysUserService;
import fun.mortnon.service.sys.vo.SysUserDTO;
import fun.mortnon.web.controller.user.command.CreateUserCommand;
import fun.mortnon.web.controller.user.command.UpdateUserCommand;
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
import java.util.Optional;
import java.util.function.Function;
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
            return Mono.error(ParameterException.create("password is empty"));
        }

        if (!createUserCommand.getPassword().equals(createUserCommand.getRepeatPassword())) {
            return Mono.error(ParameterException.create( "password is not match"));
        }

        if (StringUtils.isEmpty(createUserCommand.getUserName())) {
            return Mono.error(ParameterException.create("user name is empty"));
        }

        if (StringUtils.isEmpty(createUserCommand.getNickName())) {
            return Mono.error(ParameterException.create("nick name is empty"));
        }

        return sysUserMapper.existsByUserName(createUserCommand.getUserName())
                .flatMap(exists -> {
                    if (exists) {
                        log.info("repeat user name:{}", createUserCommand.getUserName());
                        return Mono.error(RepeatDataException.create(ErrorCodeEnum.USERNAME_ALREADY_EXISTS, "user name is repeat."));
                    }
                    SysUser sysUser = new SysUser();
                    sysUser.setUserName(createUserCommand.getUserName());
                    sysUser.setNickName(createUserCommand.getNickName());
                    sysUser.setSalt(RandomStringUtils.randomAlphanumeric(6));
                    String password = createUserCommand.getPassword();
                    password = DigestUtils.sha256Hex(password + sysUser.getSalt());
                    sysUser.setPassword(password);
                    sysUser.setSex(createUserCommand.getSex());
                    sysUser.setEmail(createUserCommand.getEmail());
                    sysUser.setPhone(createUserCommand.getPhone());

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
                        return Mono.error(NotFoundException.create("user not exists."));
                    }
                    return sysUserMapper.findByUserName(username);
                });
    }

    @Override
    public Mono<Page<SysUserDTO>> queryUsers(Pageable pageable) {
        return sysUserMapper.findAll(pageable).map(k -> {
            List<SysUserDTO> collect = k.getContent().stream().map(SysUserDTO::convert).collect(Collectors.toList());
            return Page.of(collect, k.getPageable(), k.getTotalSize());
        });
    }

    @Override
    public Mono<Boolean> deleteUser(String userName) {
        return sysUserMapper.existsByUserName(userName)
                .flatMap(result -> {
                    log.info("query user by name:{}", userName);
                    if (!result) {
                        log.info("user name {} is not exists", userName);
                        return Mono.error(NotFoundException.create("user is not exists."));
                    }
                    return sysUserMapper.deleteByUserName(userName);
                }).then(Mono.just(true));
    }

    @Override
    public Mono<SysUser> updateUser(UpdateUserCommand updateUserCommand) {
        if (StringUtils.isEmpty(updateUserCommand.getUserName())) {
            log.info("update user,name is empty.");
            return Mono.error(new MortnonBaseException(ErrorCodeEnum.USER_NAME_CHECK_FAILED, "user name is empty."));
        }
        return sysUserMapper.existsByUserName(updateUserCommand.getUserName())
                .flatMap(result -> {
                    if (!result) {
                        return Mono.error(new MortnonBaseException(ErrorCodeEnum.USER_NAME_CHECK_FAILED, "user is not exists."));
                    }
                    return sysUserMapper.findByUserName(updateUserCommand.getUserName());
                })
                .flatMap(user -> {

                    Optional.ofNullable(validateUpdate.apply(updateUserCommand.getNickName())).ifPresent(t -> user.setNickName(t));
                    Optional.ofNullable(validateUpdate.apply(updateUserCommand.getNickName())).ifPresent(t -> user.setNickName(t));
                    Optional.ofNullable(validateUpdate.apply(updateUserCommand.getEmail())).ifPresent(t -> user.setEmail(t));
                    Optional.ofNullable(validateUpdate.apply(updateUserCommand.getPhone())).ifPresent(t -> user.setPhone(t));
                    Optional.ofNullable(validateUpdate.apply(updateUserCommand.getHead())).ifPresent(t -> user.setHead(t));
                    updateUserCommand.setSex(updateUserCommand.getSex());
                    return sysUserMapper.update(user);
                });
    }

    private Function<String, String> validateUpdate = data -> {
        if (StringUtils.isNotEmpty(data)) {
            return data;
        }
        return null;
    };

    @Override
    public Mono<SysRole> queryUserRole(String userName) {
        return getUserByUsername(userName).flatMap(user -> assignmentRepository.findByUserId(user.getId()))
                .flatMap(userRole -> roleRepository.findById(userRole.getRoleId()));
    }
}

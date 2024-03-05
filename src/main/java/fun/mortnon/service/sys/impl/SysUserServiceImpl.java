package fun.mortnon.service.sys.impl;

import fun.mortnon.dal.sys.entity.SysAssignment;
import fun.mortnon.dal.sys.entity.SysProject;
import fun.mortnon.dal.sys.entity.SysRole;
import fun.mortnon.dal.sys.entity.SysUser;
import fun.mortnon.dal.sys.repository.AssignmentRepository;
import fun.mortnon.dal.sys.repository.ProjectRepository;
import fun.mortnon.dal.sys.repository.RoleRepository;
import fun.mortnon.dal.sys.repository.UserRepository;
import fun.mortnon.dal.sys.specification.Specifications;
import fun.mortnon.framework.enums.ErrorCodeEnum;
import fun.mortnon.framework.exceptions.NotFoundException;
import fun.mortnon.framework.exceptions.ParameterException;
import fun.mortnon.framework.exceptions.RepeatDataException;
import fun.mortnon.service.sys.PublicService;
import fun.mortnon.service.sys.SysUserService;
import fun.mortnon.service.sys.vo.ProjectRoleDTO;
import fun.mortnon.service.sys.vo.SysUserDTO;
import fun.mortnon.web.controller.role.command.RolePageSearch;
import fun.mortnon.web.controller.user.command.CreateUserCommand;
import fun.mortnon.web.controller.user.command.UpdatePasswordCommand;
import fun.mortnon.web.controller.user.command.UpdateUserCommand;
import fun.mortnon.web.controller.user.command.UserPageSearch;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import io.micronaut.data.repository.jpa.criteria.PredicateSpecification;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.swing.text.html.Option;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.micronaut.data.repository.jpa.criteria.PredicateSpecification.where;


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
    private UserRepository userRepository;

    @Inject
    private AssignmentRepository assignmentRepository;

    @Inject
    private RoleRepository roleRepository;

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private PublicService publicService;

    @Override
    @Transactional
    public Mono<SysUserDTO> createUser(CreateUserCommand createUserCommand) {
        if (StringUtils.isEmpty(createUserCommand.getPassword()) || StringUtils.isEmpty(createUserCommand.getRepeatPassword())) {
            return Mono.error(ParameterException.create("password is empty"));
        }

        if (!createUserCommand.getPassword().equals(createUserCommand.getRepeatPassword())) {
            return Mono.error(ParameterException.create("password is not match"));
        }

        if (StringUtils.isEmpty(createUserCommand.getUserName())) {
            return Mono.error(ParameterException.create("user name is empty"));
        }

        if (StringUtils.isEmpty(createUserCommand.getNickName())) {
            return Mono.error(ParameterException.create("nick name is empty"));
        }

        return userRepository.existsByUserName(createUserCommand.getUserName())
                .flatMap(exists -> {
                    if (exists) {
                        log.warn("create user fail,repeat user name:{}", createUserCommand.getUserName());
                        return Mono.error(RepeatDataException.create(ErrorCodeEnum.USERNAME_ALREADY_EXISTS, "user name is repeat."));
                    }
                    SysUser sysUser = new SysUser();
                    sysUser.setUserName(createUserCommand.getUserName());
                    sysUser.setNickName(createUserCommand.getNickName());
                    sysUser.setPassword(createUserCommand.getPassword());
                    sysUser.setSex(createUserCommand.getSex());
                    sysUser.setStatus(createUserCommand.isStatus());
                    sysUser.setEmail(createUserCommand.getEmail());
                    sysUser.setPhone(createUserCommand.getPhone());
                    hashPassword(sysUser);

                    return userRepository.save(sysUser).map(SysUserDTO::convert);
                })
                .flatMap(userDTO -> {
                    if (CollectionUtils.isEmpty(createUserCommand.getProjectRoles())) {
                        return Mono.just(userDTO);
                    }

                    boolean existsEmpty = createUserCommand.getProjectRoles().stream()
                            .anyMatch(k -> null == k.getProjectId() || k.getProjectId() <= 0 || null == k.getRoleId() || k.getRoleId() <= 0);
                    if (existsEmpty) {
                        return Mono.error(ParameterException.create("user project / role id is empty."));
                    }
                    return Flux.fromStream(createUserCommand.getProjectRoles().stream())
                            .flatMap(projectRoleDTO -> {
                                return projectRepository.existsById(projectRoleDTO.getProjectId())
                                        .flatMap(projectExists -> {
                                            if (!projectExists) {
                                                return Mono.error(NotFoundException.create("project is not exists."));
                                            }
                                            return roleRepository.existsById(projectRoleDTO.getRoleId());
                                        })
                                        .flatMap(roleExists -> {
                                            if (!roleExists) {
                                                return Mono.error(NotFoundException.create("role is not exists."));
                                            }
                                            return publicService.saveAssignment(userDTO.getId(), projectRoleDTO.getProjectId(), projectRoleDTO.getRoleId());
                                        });
                            })
                            .flatMap(assignment -> queryProjectRole(assignment))
                            .collectList()
                            .map(projectRoles -> {
                                userDTO.setProjectRoles(projectRoles);
                                return userDTO;
                            });
                });
    }

    /**
     * 按关联信息查询相关组织、角色数据
     *
     * @param assignment
     * @return
     */
    private Mono<ProjectRoleDTO> queryProjectRole(SysAssignment assignment) {
        ProjectRoleDTO projectRoleDTO = new ProjectRoleDTO();
        if (ObjectUtils.isNotEmpty(assignment.getProjectId())) {
            return projectRepository.findById(assignment.getProjectId())
                    .flatMap(project -> {
                        projectRoleDTO.setProjectId(project.getId());
                        projectRoleDTO.setProjectName(project.getName());
                        return roleRepository.findById(assignment.getRoleId());
                    })
                    .map(role -> {
                        projectRoleDTO.setRoleId(role.getId());
                        projectRoleDTO.setRoleName(role.getName());
                        return projectRoleDTO;
                    });
        }
        return roleRepository.findById(assignment.getRoleId())
                .map(role -> {
                    projectRoleDTO.setRoleId(role.getId());
                    projectRoleDTO.setRoleName(role.getName());
                    return projectRoleDTO;
                });
    }

    private void hashPassword(SysUser sysUser) {
        sysUser.setSalt(RandomStringUtils.randomAlphanumeric(6));
        String hashPassword = DigestUtils.sha256Hex(sysUser.getPassword() + sysUser.getSalt());
        sysUser.setPassword(hashPassword);
    }

    @Override
    public Mono<SysUserDTO> getUserById(Long id) {
        return userRepository.existsById(id)
                .flatMap(result -> {
                    if (!result) {
                        log.warn("query user fail,user id [{}] is not exists.", id);
                        return Mono.error(NotFoundException.create("user not exists."));
                    }
                    return userRepository.findById(id);
                })
                .flatMap(user -> {
                    SysUserDTO sysUserDTO = SysUserDTO.convert(user);
                    return assignmentRepository.findByUserId(user.getId())
                            .map(assignment -> {
                                ProjectRoleDTO projectRoleDTO = new ProjectRoleDTO();
                                projectRoleDTO.setRoleId(assignment.getRoleId());
                                projectRoleDTO.setProjectId(assignment.getProjectId());
                                return projectRoleDTO;
                            })
                            .collectList()
                            .map(prList -> {
                                sysUserDTO.setProjectRoles(prList);
                                return sysUserDTO;
                            });
                });
    }

    @Override
    public Mono<Page<SysUserDTO>> queryUsers(UserPageSearch pageSearch) {
        //默认按用户名正序排列
        Pageable pageable = pageSearch.convert();
        if (!pageable.isSorted()) {
            pageable = pageable.order(Sort.Order.asc("userName"));
        }
        return userRepository.findAll(where(queryCondition(pageSearch)), pageable).flatMap(k -> {
            List<SysUserDTO> collect = k.getContent().stream().map(SysUserDTO::convert).collect(Collectors.toList());

            return Flux.fromIterable(collect)
                    .flatMapSequential(userDTO -> {
                        return assignmentRepository.existsByUserId(userDTO.getId())
                                .flatMapMany(exists -> {
                                    if (!exists) {
                                        log.info("query user [{}],assignment is empty.", userDTO.getId());
                                        return Mono.empty();
                                    }

                                    return assignmentRepository.findByUserId(userDTO.getId());
                                })
                                .flatMap(assignment -> queryProjectRole(assignment))
                                .collectList()
                                .map(projectRoles -> {
                                    userDTO.setProjectRoles(projectRoles);
                                    return userDTO;
                                });
                    })
                    .flatMap(userDTO -> {
                        if (ObjectUtils.isNotEmpty(pageSearch.getProjectId())) {
                            if (userDTO.getProjectRoles().size() == 0) {
                                return Mono.empty();
                            }
                            if (!pageSearch.getProjectId().equals(userDTO.getProjectRoles().get(0).getProjectId())) {
                                return Mono.empty();
                            }
                        }
                        return Mono.just(userDTO);
                    })
                    .collectList()
                    .map(list -> Page.of(list, k.getPageable(), k.getTotalSize()));
        });
    }

    private PredicateSpecification<SysUser> queryCondition(UserPageSearch pageSearch) {
        PredicateSpecification<SysUser> query = null;

        if (StringUtils.isNotEmpty(pageSearch.getNickName())) {
            query = Specifications.propertyLike("nickName", pageSearch.getNickName());
        }

        if (StringUtils.isNotEmpty(pageSearch.getUserName())) {
            PredicateSpecification<SysUser> subQuery = Specifications.propertyLike("userName", pageSearch.getUserName());
            if (query == null) {
                query = subQuery;
            } else {
                query = query.and(subQuery);
            }
        }

        if (StringUtils.isNotEmpty(pageSearch.getPhone())) {
            PredicateSpecification<SysUser> subQuery = Specifications.propertyLike("phone", pageSearch.getPhone());
            if (query == null) {
                query = subQuery;
            } else {
                query = query.and(subQuery);
            }
        }

        if (ObjectUtils.isNotEmpty(pageSearch.getStatus())) {
            PredicateSpecification<SysUser> subQuery = Specifications.propertyEqual("status", pageSearch.getStatus());
            if (query == null) {
                query = subQuery;
            } else {
                query = query.and(subQuery);
            }
        }


        return query;
    }


    @Override
    public Mono<SysUser> getUserByUsername(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public Mono<Boolean> deleteUser(Long id) {
        if (id == 1) {
            return Mono.error(ParameterException.create("default data can't be deleted."));
        }
        return userRepository.existsById(id)
                .flatMap(result -> {
                    if (!result) {
                        log.warn("delete user fail,user id [{}] is not exists", id);
                        return Mono.error(NotFoundException.create("user is not exists."));
                    }
                    return assignmentRepository.deleteByUserId(id);
                })
                .flatMap(result -> userRepository.deleteById(id))
                .map(k -> k > 0);
    }

    @Override
    public Mono<Boolean> batchDeleteUser(String ids) {
        List<Long> idList = Arrays.asList(ids.split(",")).stream().map(Long::parseLong).collect(Collectors.toList());
        if (idList.contains(1L)) {
            idList.remove(1L);
        }
        return assignmentRepository.deleteByUserIdInList(idList)
                .flatMap(resultTotal -> userRepository.deleteByIdInList(idList))
                .map(result -> result > 0);
    }

    @Override
    public Mono<SysUserDTO> updateUser(UpdateUserCommand updateUserCommand) {
        return userRepository.existsById(updateUserCommand.getId())
                .flatMap(result -> {
                    if (!result) {
                        log.warn("update user fail,user id [{}] is not exists.", updateUserCommand.getId());
                        return Mono.error(NotFoundException.create("user is not exists."));
                    }
                    return userRepository.findById(updateUserCommand.getId());
                })
                .flatMap(user -> {
                    Optional.ofNullable(validateUpdate.apply(updateUserCommand.getNickName())).ifPresent(t -> user.setNickName(t));
                    Optional.ofNullable(validateUpdate.apply(updateUserCommand.getEmail())).ifPresent(t -> user.setEmail(t));
                    Optional.ofNullable(validateUpdate.apply(updateUserCommand.getPhone())).ifPresent(t -> user.setPhone(t));
                    Optional.ofNullable(updateUserCommand.getStatus()).ifPresent(t-> user.setStatus(t));
                    user.setSex(updateUserCommand.getSex());

                    return userRepository.update(user);
                })
                .map(SysUserDTO::convert);
    }

    private Function<String, String> validateUpdate = data -> {
        if (StringUtils.isNotEmpty(data)) {
            return data;
        }
        return null;
    };

    @Override
    public Flux<SysRole> queryUserRole(String userName) {
        return userRepository.findByUserName(userName)
                .flatMapMany(user -> assignmentRepository.findByUserId(user.getId()))
                .flatMap(assignment -> roleRepository.findById(assignment.getRoleId()));
    }

    @Override
    public Flux<SysProject> queryUserProject(String userName) {
        return userRepository.findByUserName(userName)
                .flatMapMany(user -> assignmentRepository.findByUserId(user.getId()))
                .flatMap(assignment -> projectRepository.findById(assignment.getProjectId()));
    }

    @Override
    public Mono<Boolean> updateUserPassword(UpdatePasswordCommand updatePasswordCommand) {
        if (null == updatePasswordCommand.getId() || updatePasswordCommand.getId() <= 0) {
            return Mono.error(ParameterException.create("user info is incorrect."));
        }
        if (!updatePasswordCommand.getPassword().equals(updatePasswordCommand.getRepeatPassword())) {
            return Mono.error(ParameterException.create("password and repeat password is not match."));
        }

        return userRepository.existsById(updatePasswordCommand.getId())
                .flatMap(exists -> {
                    if (!exists) {
                        log.warn("update password fail,user id [{}] is not exists.", updatePasswordCommand.getId());
                        return Mono.error(NotFoundException.create("user id is not exists."));
                    }
                    return userRepository.findById(updatePasswordCommand.getId());
                })
                .flatMap(user -> {
                    user.setPassword(updatePasswordCommand.getPassword());
                    hashPassword(user);
                    return userRepository.update(user);
                })
                .map(result -> result != null);
    }

    @Override
    public Mono<Boolean> updateSelfPassword(UpdatePasswordCommand updatePasswordCommand) {
        if (StringUtils.isEmpty(updatePasswordCommand.getUserName())) {
            return Mono.error(ParameterException.create("user info is incorrect."));
        }

        if (StringUtils.isEmpty(updatePasswordCommand.getOldPassword())) {
            return Mono.error(ParameterException.create("old password is empty."));
        }

        if (!updatePasswordCommand.getPassword().equals(updatePasswordCommand.getRepeatPassword())) {
            return Mono.error(ParameterException.create("password and repeat password is not match."));
        }

        return userRepository.existsByUserName(updatePasswordCommand.getUserName())
                .flatMap(exists -> {
                    if (!exists) {
                        log.warn("update password fail,user name [{}] is not exists.", updatePasswordCommand.getUserName());
                        return Mono.error(NotFoundException.create("user is not exists."));
                    }
                    return userRepository.findByUserName(updatePasswordCommand.getUserName());
                })
                .flatMap(user -> {
                    user.setPassword(updatePasswordCommand.getPassword());
                    hashPassword(user);
                    return userRepository.update(user);
                })
                .map(result -> result != null);
    }
}

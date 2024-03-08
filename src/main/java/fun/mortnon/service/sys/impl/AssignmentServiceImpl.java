package fun.mortnon.service.sys.impl;

import fun.mortnon.dal.sys.entity.SysAssignment;
import fun.mortnon.dal.sys.entity.SysUser;
import fun.mortnon.dal.sys.repository.AssignmentRepository;
import fun.mortnon.dal.sys.repository.ProjectRepository;
import fun.mortnon.dal.sys.repository.RoleRepository;
import fun.mortnon.dal.sys.repository.UserRepository;
import fun.mortnon.dal.sys.specification.Specifications;
import fun.mortnon.framework.enums.ErrorCodeEnum;
import fun.mortnon.framework.exceptions.NotFoundException;
import fun.mortnon.framework.exceptions.ParameterException;
import fun.mortnon.service.sys.AssignmentService;
import fun.mortnon.service.sys.PublicService;
import fun.mortnon.service.sys.vo.ProjectRoleDTO;
import fun.mortnon.service.sys.vo.SysUserDTO;
import fun.mortnon.web.controller.user.command.AssignmentCommand;
import fun.mortnon.web.controller.user.command.RevokeCommand;
import fun.mortnon.web.controller.user.command.UserPageSearch;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import io.micronaut.data.repository.jpa.criteria.PredicateSpecification;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.micronaut.data.repository.jpa.criteria.PredicateSpecification.where;

/**
 * @author dev2007
 * @date 2023/2/27
 */
@Singleton
@Slf4j
public class AssignmentServiceImpl implements AssignmentService {
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
    public Mono<Page<SysUserDTO>> queryAssignmentUser(UserPageSearch pageSearch) {
        boolean isSearchProject = false;
        boolean isSearchRole = false;

        if (ObjectUtils.isNotEmpty(pageSearch.getProjectId())) {
            isSearchProject = true;
        }

        if (ObjectUtils.isNotEmpty(pageSearch.getRoleId())) {
            isSearchRole = true;
        }

        if (!isSearchProject && !isSearchRole) {
            isSearchProject = true;
        }


        if (isSearchProject) {
            return assignmentRepository.findByProjectIdIsNotNull()
                    .collectList()
                    .flatMap(assignmentList -> {
                        List<Long> assignmentUserList = assignmentList.stream().filter(assignment -> {
                                    if (pageSearch.isUnassignment()) {
                                        return true;
                                    }
                                    return assignment.getProjectId().equals(pageSearch.getProjectId());
                                }).map(SysAssignment::getUserId)
                                .collect(Collectors.toList());
                        return queryUserAssignmentUser(pageSearch, assignmentUserList);
                    })
                    .flatMap(page -> convertUserData(page));
        }

        return assignmentRepository.findByRoleIdIsNotNull()
                .collectList()
                .flatMap(assignmentList -> {
                    List<Long> assignmentUserList = assignmentList.stream().filter(assignment -> {
                                if (pageSearch.isUnassignment()) {
                                    return true;
                                }
                                return assignment.getRoleId().equals(pageSearch.getRoleId());
                            }).map(SysAssignment::getUserId)
                            .collect(Collectors.toList());
                    return queryUserAssignmentUser(pageSearch, assignmentUserList);
                })
                .flatMap(page -> convertUserData(page));
    }

    private Mono<Page<SysUserDTO>> convertUserData(Page<SysUser> page) {
        List<SysUserDTO> list = page.getContent().stream().map(SysUserDTO::convert).collect(Collectors.toList());
        return Flux.fromIterable(list)
                .flatMapSequential(user ->
                        assignmentRepository.findByUserId(user.getId())
                                .flatMap(assignment -> {
                                    if (ObjectUtils.isNotEmpty(assignment.getProjectId())) {
                                        return projectRepository.findById(assignment.getProjectId())
                                                .map(project -> {
                                                    ProjectRoleDTO projectRoleDTO = new ProjectRoleDTO();
                                                    projectRoleDTO.setProjectId(project.getId());
                                                    projectRoleDTO.setProjectName(project.getName());
                                                    return projectRoleDTO;
                                                })
                                                .flatMap(projectRoleDTO -> {
                                                    if (ObjectUtils.isNotEmpty(assignment.getRoleId())) {
                                                        return roleRepository.findById(assignment.getRoleId())
                                                                .map(role -> {
                                                                    projectRoleDTO.setRoleId(role.getId());
                                                                    projectRoleDTO.setRoleName(role.getName());
                                                                    return projectRoleDTO;
                                                                });
                                                    }
                                                    return Mono.just(projectRoleDTO);
                                                });
                                    }

                                    if (ObjectUtils.isNotEmpty(assignment.getRoleId())) {
                                        return roleRepository.findById(assignment.getRoleId())
                                                .map(role -> {
                                                    ProjectRoleDTO projectRoleDTO = new ProjectRoleDTO();
                                                    projectRoleDTO.setRoleId(role.getId());
                                                    projectRoleDTO.setRoleName(role.getName());
                                                    return projectRoleDTO;
                                                });
                                    }

                                    return Mono.empty();
                                })
                                .collectList()
                                .map(projectRoleDTOS -> {
                                    user.setProjectRoles(projectRoleDTOS);
                                    return user;
                                })
                )
                .collectList()
                .map(userDTOList -> {
                    return Page.of(userDTOList, page.getPageable(), page.getTotalSize());
                });
    }

    private Mono<Page<SysUser>> queryUserAssignmentUser(UserPageSearch pageSearch, List<Long> assignmentUserList) {
        //默认按用户名正序排列
        Pageable pageable = pageSearch.convert();
        if (!pageable.isSorted()) {
            pageable = pageable.order(Sort.Order.asc("userName"));
        }

        //查找未分配的
        if (pageSearch.isUnassignment()) {
            return userRepository.findAll(where(queryCondition(pageSearch, null, assignmentUserList)), pageable);
        }
        //查找已分配的
        return userRepository.findAll(where(queryCondition(pageSearch, assignmentUserList, null)), pageable);
    }

    /**
     * 组合查询条件
     *
     * @param search
     * @param includeUserIdList 要包含在内的用户
     * @param excludeUserIdList 要排除的用户
     * @return
     */
    private PredicateSpecification<SysUser> queryCondition(UserPageSearch search, List<Long> includeUserIdList,
                                                           List<Long> excludeUserIdList) {
        PredicateSpecification<SysUser> query = null;

        if (includeUserIdList != null) {
            //如果过滤数据完全为空，给定一个无效值
            if (CollectionUtils.isEmpty(includeUserIdList)) {
                includeUserIdList.add(-1L);
            }
            query = Specifications.idInList("id", includeUserIdList);
        }

        if (excludeUserIdList != null) {
            //如果过滤数据完全为空，给定一个无效值
            if (CollectionUtils.isEmpty(excludeUserIdList)) {
                excludeUserIdList.add(-1L);
            }
            query = Specifications.idNotInList("id", excludeUserIdList);
        }

        if (StringUtils.isNotEmpty(search.getUserName())) {
            PredicateSpecification<SysUser> subQuery = Specifications.propertyLike("userName", search.getUserName());
            if (query == null) {
                query = subQuery;
            } else {
                query = query.and(subQuery);
            }
        }

        if (ObjectUtils.isNotEmpty(search.getNickName())) {
            PredicateSpecification<SysUser> subQuery = Specifications.propertyLike("nickName", search.getNickName());
            if (query == null) {
                query = subQuery;
            } else {
                query = query.and(subQuery);
            }
        }

        if (ObjectUtils.isNotEmpty(search.getSex())) {
            PredicateSpecification<SysUser> subQuery = Specifications.propertyEqual("sex", search.getSex());
            if (query == null) {
                query = subQuery;
            } else {
                query = query.and(subQuery);
            }
        }

        if (ObjectUtils.isNotEmpty(search.getEmail())) {
            PredicateSpecification<SysUser> subQuery = Specifications.propertyLike("email", search.getEmail());
            if (query == null) {
                query = subQuery;
            } else {
                query = query.and(subQuery);
            }
        }

        if (ObjectUtils.isNotEmpty(search.getPhone())) {
            PredicateSpecification<SysUser> subQuery = Specifications.propertyLike("phone", search.getPhone());
            if (query == null) {
                query = subQuery;
            } else {
                query = query.and(subQuery);
            }
        }

        return query;
    }

    @Override
    public Mono<Boolean> assignmentUser(AssignmentCommand assignmentCommand) {
        if ((ObjectUtils.isEmpty(assignmentCommand.getUserId()) || assignmentCommand.getUserId() <= 0)
                && CollectionUtils.isEmpty(assignmentCommand.getUserIdList())) {
            return Mono.error(ParameterException.create(ErrorCodeEnum.USER_INFO_ERROR));
        }

        if (assignmentCommand.getUserId() == 1) {
            log.warn("Changing the default user is prohibited.");
            return Mono.error(ParameterException.create(ErrorCodeEnum.DEFAULT_USER_FORBID_DELETE));
        }

        if (CollectionUtils.isEmpty(assignmentCommand.getUserIdList())) {
            assignmentCommand.setUserIdList(new ArrayList<>());
            assignmentCommand.getUserIdList().add(assignmentCommand.getUserId());
        }

        final boolean isProject = ObjectUtils.isNotEmpty(assignmentCommand.getProjectId());

        final boolean isRole = ObjectUtils.isNotEmpty(assignmentCommand.getRoleId());

        if (!isProject && !isRole) {
            log.warn("Organization and role data are error");
            return Mono.error(ParameterException.create(ErrorCodeEnum.PROJECT_ROLE_DATA_ERROR));
        }

        return validateAssignment(assignmentCommand, isProject, isRole)
                .flatMap(exists -> {
                    //组织或角色id不存在
                    if (!exists) {
                        return Mono.error(NotFoundException.create(isProject ? ErrorCodeEnum.PROJECT_ERROR : ErrorCodeEnum.ROLE_ERROR));
                    }

                    //处理已部分分配的用户数据
                    return assignmentRepository.findByUserIdIn(assignmentCommand.getUserIdList())
                            .flatMap(assignment -> {
                                if (isProject) {
                                    assignment.setProjectId(assignmentCommand.getProjectId());
                                }
                                if (isRole) {
                                    assignment.setRoleId(assignmentCommand.getRoleId());
                                }
                                return assignmentRepository.update(assignment);
                            })
                            .collectList()
                            .map(assignmentList -> {
                                //处理完全未分配的用户数据
                                List<Long> userIdList = assignmentCommand.getUserIdList();

                                return userIdList.stream().filter(userId -> assignmentList.stream().anyMatch(assignment -> !assignment.getUserId().equals(userId)))
                                        .map(userId -> {
                                            return new SysAssignment(userId, assignmentCommand.getProjectId(),
                                                    assignmentCommand.getRoleId());
                                        })
                                        .collect(Collectors.toList());
                            })
                            .flatMap(newAssignmentList ->
                                    Flux.fromIterable(newAssignmentList)
                                            .flatMap(assignment -> assignmentRepository.save(assignment))
                                            .collectList()
                            )
                            .map(list -> list.size() > 0);
                });
    }

    private Mono<Boolean> validateAssignment(AssignmentCommand assignmentCommand, final boolean isProject, boolean isRole) {
        return Mono.defer(() -> {
            if (isProject) {
                return projectRepository.existsById(assignmentCommand.getProjectId());
            }
            return Mono.just(true);
        }).flatMap(projectExists -> {
            if (isRole) {
                return roleRepository.existsById(assignmentCommand.getRoleId()).map(roleExists -> (roleExists || projectExists));
            }
            return Mono.just(true);
        });
    }

    @Override
    public Mono<Boolean> revokeUser(RevokeCommand revokeCommand) {
        if (ObjectUtils.isEmpty(revokeCommand.getUserId()) || revokeCommand.getUserId() <= 0) {
            return Mono.error(ParameterException.create(ErrorCodeEnum.USER_INFO_ERROR));
        }

        if (revokeCommand.getUserId() == 1) {
            log.warn("Changing the default user is prohibited.");
            return Mono.error(ParameterException.create(ErrorCodeEnum.DEFAULT_USER_FORBID_DELETE));
        }

        boolean isProject = true;

        if (ObjectUtils.isEmpty(revokeCommand.getProjectId()) || revokeCommand.getProjectId() <= 0) {
            isProject = false;
        }

        boolean isRole = true;

        if (ObjectUtils.isEmpty(revokeCommand.getRoleId()) || revokeCommand.getRoleId() <= 0) {
            isRole = false;
        }

        if (!isProject && !isRole) {
            return Mono.error(ParameterException.create(ErrorCodeEnum.USER_INFO_ERROR));
        }

        if (isProject && isRole) {
            return assignmentRepository.existsByUserIdEqualsAndProjectIdEqualsAndRoleIdEquals(revokeCommand.getUserId(),
                            revokeCommand.getProjectId(), revokeCommand.getRoleId())
                    .flatMap(exists -> {
                        if (!exists) {
                            log.warn("Failed to remove organization role assignment because user {}, organization {}, and role {} do not exist.", revokeCommand.getUserId(),
                                    revokeCommand.getProjectId(), revokeCommand.getRoleId());
                            return Mono.error(ParameterException.create(ErrorCodeEnum.USER_INFO_ERROR));
                        }

                        return assignmentRepository.deleteByUserIdEqualsAndProjectIdEqualsAndRoleIdEquals(revokeCommand.getUserId(),
                                revokeCommand.getProjectId(), revokeCommand.getRoleId());
                    })
                    .map(result -> result > 0);
        }

        if (isProject) {
            return assignmentRepository.existsByUserIdEqualsAndProjectIdEquals(revokeCommand.getUserId(),
                            revokeCommand.getProjectId())
                    .flatMap(exists -> {
                        if (!exists) {
                            log.warn("Failed to remove organization role assignment because [user {}], organization {} do not exist.",
                                    revokeCommand.getUserId(),
                                    revokeCommand.getProjectId());
                            return Mono.error(ParameterException.create(ErrorCodeEnum.USER_INFO_ERROR));
                        }

                        return assignmentRepository.findByUserIdEqualsAndProjectIdEquals(revokeCommand.getUserId(),
                                revokeCommand.getProjectId());
                    })
                    .flatMap(assignment -> {
                        assignment.setProjectId(null);
                        return assignmentRepository.update(assignment).map(result -> result != null);
                    });
        }

        return assignmentRepository.existsByUserIdEqualsAndRoleIdEquals(revokeCommand.getUserId(),
                        revokeCommand.getRoleId())
                .flatMap(exists -> {
                    if (!exists) {
                        log.warn("Failed to remove organization role assignment because [user {}], role {} do not exist.",
                                revokeCommand.getUserId(),
                                revokeCommand.getRoleId());
                        return Mono.error(ParameterException.create(ErrorCodeEnum.USER_INFO_ERROR));
                    }

                    return assignmentRepository.findByUserIdEqualsAndRoleIdEquals(revokeCommand.getUserId(), revokeCommand.getRoleId());
                })
                .flatMap(assignment -> {
                    assignment.setRoleId(null);
                    return assignmentRepository.update(assignment).map(result -> result != null);
                });
    }
}

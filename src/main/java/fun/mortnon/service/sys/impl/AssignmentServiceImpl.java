package fun.mortnon.service.sys.impl;

import fun.mortnon.dal.sys.entity.SysAssignment;
import fun.mortnon.dal.sys.entity.SysProject;
import fun.mortnon.dal.sys.entity.SysUser;
import fun.mortnon.dal.sys.repository.AssignmentRepository;
import fun.mortnon.dal.sys.repository.ProjectRepository;
import fun.mortnon.dal.sys.repository.RoleRepository;
import fun.mortnon.dal.sys.repository.UserRepository;
import fun.mortnon.dal.sys.specification.Specifications;
import fun.mortnon.framework.exceptions.NotFoundException;
import fun.mortnon.framework.exceptions.ParameterException;
import fun.mortnon.framework.exceptions.RepeatDataException;
import fun.mortnon.service.sys.AssignmentService;
import fun.mortnon.service.sys.PublicService;
import fun.mortnon.service.sys.vo.SysUserDTO;
import fun.mortnon.web.controller.project.command.ProjectPageSearch;
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
import reactor.core.publisher.Mono;

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

        return assignmentRepository.findByRoleId(pageSearch.getRoleId())
                .map(assignment -> assignment.getUserId())
                .collectList()
                .flatMap(userIdList -> {
                    //默认按id正序排列
                    Pageable pageable = pageSearch.convert();
                    if (!pageable.isSorted()) {
                        pageable = pageable.order(Sort.Order.asc("id"));
                    }
                    return userRepository.findAll(where(queryCondition(pageSearch, userIdList, null)), pageable);
                })
                .map(page -> {
                    List<SysUserDTO> list = page.getContent().stream().map(SysUserDTO::convert).collect(Collectors.toList());
                    return Page.of(list, page.getPageable(), page.getTotalSize());
                });
    }

    @Override
    public Mono<Page<SysUserDTO>> queryUnassignmentUser(UserPageSearch pageSearch) {
        return assignmentRepository.findByRoleIdIsNotNull()
                .map(SysAssignment::getUserId)
                .collectList()
                .flatMap(existsUserIdList -> {
                    //默认按id正序排列
                    Pageable pageable = pageSearch.convert();
                    if (!pageable.isSorted()) {
                        pageable = pageable.order(Sort.Order.asc("id"));
                    }
                    return userRepository.findAll(where(queryCondition(pageSearch, null, existsUserIdList)), pageable);
                })
                .map(page -> {
                    List<SysUserDTO> list = page.getContent().stream().map(SysUserDTO::convert).collect(Collectors.toList());
                    return Page.of(list, page.getPageable(), page.getTotalSize());
                });
    }

    private PredicateSpecification<SysUser> queryCondition(UserPageSearch search, List<Long> userIdList, List<Long> notInUserIdList) {
        PredicateSpecification<SysUser> query = null;

        if (CollectionUtils.isNotEmpty(userIdList)) {
            query = Specifications.idInList("id", userIdList);
        }

        if (CollectionUtils.isNotEmpty(notInUserIdList)) {
            query = Specifications.idNotInList("id", notInUserIdList);
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
    public Mono<Boolean> assignmentUser(Long userId, Long projectId, Long roleId) {
        if (null == userId || userId <= 0) {
            return Mono.error(ParameterException.create("user id is wrong."));
        }

        if (null == projectId || projectId <= 0) {
            return Mono.error(ParameterException.create("project id is wrong."));
        }

        if (null == roleId || roleId <= 0) {
            return Mono.error(ParameterException.create("role id is wrong."));
        }

        return userRepository.existsById(userId)
                .flatMap(exists -> {
                    if (!exists) {
                        log.warn("user id [{}] is not exists.", userId);
                        return Mono.error(NotFoundException.create("user id is not exists."));
                    }
                    return projectRepository.existsById(projectId);
                })
                .flatMap(exists -> {
                    if (!exists) {
                        log.warn("project id [{}] is not exists.");
                        return Mono.error(NotFoundException.create("project id is not exists."));
                    }
                    return roleRepository.existsById(roleId);
                })
                .flatMap(exists -> {
                    if (!exists) {
                        log.warn("role id [{}] is not exists.", roleId);
                        return Mono.error(NotFoundException.create("role id is not exists."));
                    }
                    return assignmentRepository.existsByUserIdEqualsAndProjectIdEqualsAndRoleIdEquals(userId, projectId, roleId);
                })
                .flatMap(exists -> {
                    if (exists) {
                        log.warn("assignment,user [{}],project [{}],role [{}],is exists.", userId, projectId, roleId);
                        return Mono.error(RepeatDataException.create("assignment is exists."));
                    }

                    return publicService.saveAssignment(userId, projectId, roleId);
                }).thenReturn(true);
    }

    @Override
    public Mono<Boolean> revokeUser(RevokeCommand revokeCommand) {
        if (ObjectUtils.isEmpty(revokeCommand.getUserId()) || revokeCommand.getUserId() <= 0) {
            return Mono.error(ParameterException.create("user id is wrong."));
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
            return Mono.error(ParameterException.create("project and role id all are wrong."));
        }

        if (isProject && isProject) {
            return assignmentRepository.existsByUserIdEqualsAndProjectIdEqualsAndRoleIdEquals(revokeCommand.getUserId(),
                            revokeCommand.getProjectId(), revokeCommand.getRoleId())
                    .flatMap(exists -> {
                        if (!exists) {
                            log.warn("revoke assignment,user [{}],project [{}],role [{}],is not exists.", revokeCommand.getUserId(),
                                    revokeCommand.getProjectId(), revokeCommand.getRoleId());
                            return Mono.error(RepeatDataException.create("assignment data is not exists."));
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
                            log.warn("revoke assignment,user [{}],project [{}],is not exists.", revokeCommand.getUserId(),
                                    revokeCommand.getProjectId());
                            return Mono.error(RepeatDataException.create("assignment data is not exists."));
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
                        log.warn("revoke assignment,user [{}],role [{}],is not exists.", revokeCommand.getUserId(),
                                revokeCommand.getRoleId());
                        return Mono.error(RepeatDataException.create("assignment data is not exists."));
                    }

                    return assignmentRepository.findByUserIdEqualsAndRoleIdEquals(revokeCommand.getUserId(), revokeCommand.getRoleId());
                })
                .flatMap(assignment -> {
                    assignment.setRoleId(null);
                    return assignmentRepository.update(assignment).map(result -> result != null);
                });
    }
}

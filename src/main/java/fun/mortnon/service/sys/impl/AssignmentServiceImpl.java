package fun.mortnon.service.sys.impl;

import fun.mortnon.dal.sys.repository.AssignmentRepository;
import fun.mortnon.dal.sys.repository.ProjectRepository;
import fun.mortnon.dal.sys.repository.RoleRepository;
import fun.mortnon.dal.sys.repository.UserRepository;
import fun.mortnon.framework.exceptions.NotFoundException;
import fun.mortnon.framework.exceptions.ParameterException;
import fun.mortnon.framework.exceptions.RepeatDataException;
import fun.mortnon.service.sys.AssignmentService;
import fun.mortnon.service.sys.PublicService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

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
    public Mono<Boolean> revokeUser(Long userId, Long projectId, Long roleId) {
        if (null == userId || userId <= 0) {
            return Mono.error(ParameterException.create("user id is wrong."));
        }

        if (null == projectId || projectId <= 0) {
            return Mono.error(ParameterException.create("project id is wrong."));
        }

        if (null == roleId || roleId <= 0) {
            return Mono.error(ParameterException.create("role id is wrong."));
        }

        return assignmentRepository.existsByUserIdEqualsAndProjectIdEqualsAndRoleIdEquals(userId, projectId, roleId)
                .flatMap(exists -> {
                    if (!exists) {
                        log.warn("revoke assignment,user [{}],project [{}],role [{}],is not exists.", userId, projectId, roleId);
                        return Mono.error(RepeatDataException.create("assignment data is not exists."));
                    }

                    return assignmentRepository.deleteByUserIdEqualsAndProjectIdEqualsAndRoleIdEquals(userId, projectId, roleId);
                })
                .map(result -> result > 0);
    }
}

package fun.mortnon.service.sys.impl;

import fun.mortnon.dal.sys.entity.SysApi;
import fun.mortnon.dal.sys.entity.SysPermission;
import fun.mortnon.dal.sys.entity.SysRolePermission;
import fun.mortnon.dal.sys.repository.ApiRepository;
import fun.mortnon.dal.sys.repository.AssignmentRepository;
import fun.mortnon.dal.sys.repository.PermissionRepository;
import fun.mortnon.dal.sys.repository.RolePermissionRepository;
import fun.mortnon.framework.enums.ErrorCodeEnum;
import fun.mortnon.framework.exceptions.RepeatDataException;
import fun.mortnon.framework.exceptions.UsedException;
import fun.mortnon.service.sys.SysPermissionService;
import fun.mortnon.service.sys.vo.SysPermissionDTO;
import fun.mortnon.web.controller.role.command.CreatePermissionCommand;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dev2007
 * @date 2023/2/20
 */
@Singleton
@Slf4j
public class SysPermissionServiceImpl implements SysPermissionService {
    @Inject
    private PermissionRepository permissionRepository;

    @Inject
    private RolePermissionRepository rolePermissionRepository;

    @Inject
    private AssignmentRepository assignmentRepository;

    @Inject
    private ApiRepository apiRepository;

    @Override
    public Mono<Page<SysPermissionDTO>> queryPermission(Pageable pageable) {
        return permissionRepository.findAll(pageable).map(k -> {
            List<SysPermissionDTO> collect = k.getContent().stream().map(SysPermissionDTO::convert).collect(Collectors.toList());
            return Page.of(collect, k.getPageable(), k.getTotalSize());
        });
    }

    @Override
    public Mono<SysPermissionDTO> createPermission(CreatePermissionCommand createPermissionCommand) {
        SysPermission sysPermission = new SysPermission();
        sysPermission.setName(createPermissionCommand.getName());
        sysPermission.setIdentifier(createPermissionCommand.getIdentifier());
        sysPermission.setDescription(createPermissionCommand.getDescription());

        SysApi sysApi = new SysApi();
        sysApi.setApi(createPermissionCommand.getApi());
        sysApi.setMethod(createPermissionCommand.getMethod());

        return permissionRepository.existsByNameEqualsOrIdentifierEquals(createPermissionCommand.getName(),
                        createPermissionCommand.getIdentifier())
                .flatMap(exists -> {
                    if (exists) {
                        log.warn("Failed to create permission, permission [{}] or identifier [{}] is empty.",
                                createPermissionCommand.getName(), createPermissionCommand.getIdentifier());
                        return Mono.error(RepeatDataException.create(ErrorCodeEnum.PERMISSION_NAME_REPEAT));
                    }
                    return apiRepository.existsByIdentifierAndMethod(createPermissionCommand.getIdentifier(), createPermissionCommand.getMethod());

                })
                .flatMap(apiExists -> {
                    if (apiExists) {
                        log.warn("API [{},{}] has already been registered.", createPermissionCommand.getApi(), createPermissionCommand.getMethod());
                        return Mono.error(RepeatDataException.create(ErrorCodeEnum.API_REGISTERED));
                    }
                    return permissionRepository.save(sysPermission);
                })
                .map(SysPermissionDTO::convert);
    }

    @Override
    public Mono<Boolean> deletePermission(Long id) {
        return rolePermissionRepository.existsByPermissionId(id).flatMap(exists -> {
            if (exists) {
                log.warn("Failed to delete permission, permission [{}] has been used by a role.", id);
                return Mono.error(UsedException.create(ErrorCodeEnum.PERMISSION_USED));
            }

            return permissionRepository.deleteById(id).map(result -> result.intValue() > 0);
        });
    }

    @Override
    public Mono<List<SysPermissionDTO>> queryUserPermission(Long userId) {
        return assignmentRepository.findByUserId(userId)
                .map(assignment -> assignment.getRoleId())
                .flatMap(roleId -> rolePermissionRepository.findByRoleId(roleId))
                .map(SysRolePermission::getPermissionId)
                .collectList()
                .flatMap(idList -> permissionRepository.findByIdIn(idList).collectList())
                .map(permissionList -> permissionList.stream().map(SysPermissionDTO::convert).collect(Collectors.toList()));
    }
}

package fun.mortnon.service.sys.impl;

import fun.mortnon.dal.sys.entity.SysPermission;
import fun.mortnon.dal.sys.repository.PermissionRepository;
import fun.mortnon.dal.sys.repository.RolePermissionRepository;
import fun.mortnon.framework.exceptions.ParameterException;
import fun.mortnon.framework.exceptions.RepeatDataException;
import fun.mortnon.framework.exceptions.UsedException;
import fun.mortnon.service.sys.SysPermissionService;
import fun.mortnon.service.sys.vo.SysPermissionDTO;
import fun.mortnon.service.sys.vo.SysUserDTO;
import fun.mortnon.web.controller.role.command.CreatePermissionCommand;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    @Override
    public Mono<Page<SysPermissionDTO>> queryPermission(Pageable pageable) {
        return permissionRepository.findAll(pageable).map(k -> {
            List<SysPermissionDTO> collect = k.getContent().stream().map(SysPermissionDTO::convert).collect(Collectors.toList());
            return Page.of(collect, k.getPageable(), k.getTotalSize());
        });
    }

    @Override
    public Mono<SysPermissionDTO> createPermission(CreatePermissionCommand createPermissionCommand) {
        if (StringUtils.isEmpty(createPermissionCommand.getName())) {
            return Mono.error(ParameterException.create("name is empty."));
        }

        if (StringUtils.isEmpty(createPermissionCommand.getIdentifier())) {
            return Mono.error(ParameterException.create("identifier is empty."));
        }

        if (StringUtils.isEmpty(createPermissionCommand.getApi())) {
            return Mono.error(ParameterException.create("api is empty."));
        }

        if (StringUtils.isEmpty(createPermissionCommand.getMethod())) {
            return Mono.error(ParameterException.create("method is empty."));
        }

        SysPermission sysPermission = new SysPermission();
        sysPermission.setName(createPermissionCommand.getName());
        sysPermission.setIdentifier(createPermissionCommand.getIdentifier());
        sysPermission.setDescription(createPermissionCommand.getDescription());
        sysPermission.setApi(createPermissionCommand.getApi());
        sysPermission.setMethod(createPermissionCommand.getMethod());

        return permissionRepository.existsByNameEqualsOrIdentifierEquals(createPermissionCommand.getName(),
                        createPermissionCommand.getIdentifier())
                .flatMap(exists -> {
                    if (exists) {
                        log.warn("create permission fail,permission name [{}],or identifier [{}] is empty.",
                                createPermissionCommand.getName(), createPermissionCommand.getIdentifier());
                        return Mono.error(RepeatDataException.create("permission name or identifier is repeat."));
                    }
                    return permissionRepository.save(sysPermission).map(SysPermissionDTO::convert);
                });
    }

    @Override
    public Mono<Boolean> deletePermission(Long id) {
        return rolePermissionRepository.existsByPermissionId(id).flatMap(exists -> {
            if (exists) {
                log.warn("delete permission fail,permission [] is used by role.");
                return Mono.error(UsedException.create("permission is used by role."));
            }

            return permissionRepository.deleteById(id).map(result -> result.intValue() > 0);
        });
    }
}

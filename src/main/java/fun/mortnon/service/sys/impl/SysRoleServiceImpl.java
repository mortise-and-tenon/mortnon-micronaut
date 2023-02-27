package fun.mortnon.service.sys.impl;

import fun.mortnon.dal.sys.entity.SysRole;
import fun.mortnon.dal.sys.entity.SysRolePermission;
import fun.mortnon.dal.sys.repository.AssignmentRepository;
import fun.mortnon.dal.sys.repository.PermissionRepository;
import fun.mortnon.dal.sys.repository.RolePermissionRepository;
import fun.mortnon.dal.sys.repository.RoleRepository;
import fun.mortnon.framework.exceptions.NotFoundException;
import fun.mortnon.framework.exceptions.ParameterException;
import fun.mortnon.framework.exceptions.RepeatDataException;
import fun.mortnon.framework.exceptions.UsedException;
import fun.mortnon.service.sys.SysRoleService;
import fun.mortnon.service.sys.vo.SysPermissionDTO;
import fun.mortnon.service.sys.vo.SysRoleDTO;
import fun.mortnon.web.controller.role.command.CreateRoleCommand;
import fun.mortnon.web.controller.role.command.UpdateRoleCommand;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dev2007
 * @date 2023/2/15
 */
@Singleton
@Slf4j
public class SysRoleServiceImpl implements SysRoleService {
    @Inject
    private RoleRepository roleRepository;

    @Inject
    private RolePermissionRepository rolePermissionRepository;

    @Inject
    private PermissionRepository permissionRepository;

    @Inject
    private AssignmentRepository assignmentRepository;

    @Override
    @Transactional
    public Mono<SysRoleDTO> createRole(CreateRoleCommand createRoleCommand) {
        if (StringUtils.isEmpty(createRoleCommand.getName())) {
            return Mono.error(ParameterException.create("role name is empty."));
        }

        if (StringUtils.isEmpty(createRoleCommand.getIdentifier())) {
            return Mono.error(ParameterException.create("role identifier is empty."));
        }

        return roleRepository.existsByNameOrIdentifier(createRoleCommand.getName(), createRoleCommand.getIdentifier())
                .flatMap(result -> {
                    if (result) {
                        log.warn("create role fail,repeat role: {},{}", createRoleCommand.getName(), createRoleCommand.getIdentifier());
                        return Mono.error(RepeatDataException.create("repeat role name / identifier"));
                    }

                    SysRole sysRole = new SysRole();
                    sysRole.setName(createRoleCommand.getName());
                    sysRole.setIdentifier(createRoleCommand.getIdentifier());
                    sysRole.setDescription(createRoleCommand.getDescription());

                    return roleRepository.save(sysRole);
                })
                .flatMap(role -> {
                    List<SysRolePermission> rolePermissionList = new ArrayList<>();
                    createRoleCommand.getPermissionList().forEach(k -> {
                        SysRolePermission rolePermission = new SysRolePermission();
                        rolePermission.setRoleId(role.getId());
                        rolePermission.setPermissionId(k);
                        rolePermissionList.add(rolePermission);
                    });

                    SysRoleDTO roleData = SysRoleDTO.convert(role);

                    return rolePermissionRepository.saveAll(rolePermissionList)
                            .collectList()
                            .flatMapMany(list ->
                                    permissionRepository.findByIdIn(createRoleCommand.getPermissionList())
                            ).collectList()
                            .map(list -> {
                                if (list.size() != rolePermissionList.size()) {
                                    log.warn("create role fail,contains error permission id.");
                                    return Mono.error(NotFoundException.create("error permission id"));
                                }
                                List<SysPermissionDTO> pList = list.stream().map(k -> SysPermissionDTO.convert(k)).collect(Collectors.toList());
                                roleData.setPermissions(pList);
                                return roleData;
                            }).then(Mono.just(roleData));
                });

    }

    @Override
    @Transactional
    public Mono<SysRoleDTO> updateRole(UpdateRoleCommand updateRoleCommand) {
        if (CollectionUtils.isEmpty(updateRoleCommand.getPermissionList())) {
            return Mono.error(ParameterException.create("role permission is empty."));
        }

        return roleRepository.existsById(updateRoleCommand.getId())
                .flatMap(exists -> {
                    if (!exists) {
                        log.warn("update role fail,role id {} is not exists.");
                        return Mono.error(NotFoundException.create("role is not exists."));
                    }
                    return roleRepository.existsByIdNotEqualsAndNameEquals(updateRoleCommand.getId(), updateRoleCommand.getName());
                })
                .flatMap(result -> {
                    if (result) {
                        log.info("repeat role name [{}] for role {}", updateRoleCommand.getName(), updateRoleCommand.getId());
                        return Mono.error(RepeatDataException.create("role name is used."));
                    }
                    return roleRepository.findById(updateRoleCommand.getId());
                })
                .flatMap(role -> {
                    if (StringUtils.isNotEmpty(updateRoleCommand.getName())) {
                        role.setName(updateRoleCommand.getName());
                    }

                    if (StringUtils.isNotEmpty(updateRoleCommand.getDescription())) {
                        role.setDescription(updateRoleCommand.getDescription());
                    }

                    SysRoleDTO roleData = SysRoleDTO.convert(role);

                    return roleRepository.update(role)
                            .flatMap(newRole ->
                                    rolePermissionRepository.deleteByRoleId(updateRoleCommand.getId())
                            )
                            .flatMapMany(result -> {
                                List<SysRolePermission> pList = updateRoleCommand.getPermissionList().stream().map(k -> {
                                    SysRolePermission sysRolePermission = new SysRolePermission();
                                    sysRolePermission.setRoleId(updateRoleCommand.getId());
                                    sysRolePermission.setPermissionId(k);
                                    return sysRolePermission;
                                }).collect(Collectors.toList());

                                return rolePermissionRepository.saveAll(pList);
                            })
                            .collectList()
                            .flatMapMany(rpList ->
                                    permissionRepository.findByIdIn(updateRoleCommand.getPermissionList())
                            )
                            .collectList()
                            .map(list -> {
                                List<SysPermissionDTO> pList = list.stream().map(SysPermissionDTO::convert).collect(Collectors.toList());
                                roleData.setPermissions(pList);
                                return roleData;
                            });
                });
    }

    @Override
    public Mono<Page<SysRoleDTO>> queryRoles(Pageable pageable) {
        return roleRepository.findAll(pageable).flatMap(page ->
                //将分页数据中的原始角色信息转换为 DTO，并循环角色 DTO 进行数据处理
                Flux.fromStream(page.getContent().stream().map(SysRoleDTO::convert))
                        .flatMap(role ->
                                //查询角色对应的权限数据，并添加到角色 DTO 中
                                rolePermissionRepository.findByRoleId(role.getId())
                                        .flatMap(rp -> permissionRepository.findById(rp.getPermissionId()))
                                        //将 Flux 转为 Mono 包装的 List 权限数据
                                        .collectList()
                                        //将权限 List 添加给角色 DTO
                                        .map(pList -> {
                                            List<SysPermissionDTO> collect = pList.stream().map(SysPermissionDTO::convert).collect(Collectors.toList());
                                            role.setPermissions(collect);
                                            return role;
                                        })
                        )
                        //将 Flux 转为 Mono 包装的 List 角色 DTO
                        .collectList()
                        //转换为分页数据
                        .map(roleList -> Page.of(roleList, page.getPageable(), page.getTotalSize()))
        );
    }

    @Override
    public Mono<SysRoleDTO> queryRole(Long id) {
        return roleRepository.existsById(id)
                .flatMap(result -> {
                    if (!result) {
                        log.warn("query role fail,role {} is not exists.", id);
                        return Mono.error(NotFoundException.create("role is not exists."));
                    }

                    return roleRepository.findById(id)
                            .map(SysRoleDTO::convert)
                            .flatMap(role ->
                                    //查询角色对应的权限数据，并添加到角色 DTO 中
                                    rolePermissionRepository.findByRoleId(role.getId())
                                            .flatMap(rp -> permissionRepository.findById(rp.getPermissionId()))
                                            //将 Flux 转为 Mono 包装的 List 权限数据
                                            .collectList()
                                            //将权限 List 添加给角色 DTO
                                            .map(pList -> {
                                                List<SysPermissionDTO> collect = pList.stream().map(SysPermissionDTO::convert).collect(Collectors.toList());
                                                role.setPermissions(collect);
                                                return role;
                                            })
                            );
                });

    }

    @Override
    @Transactional
    public Mono<Boolean> deleteRole(Long id) {
        return roleRepository.existsById(id)
                .flatMap(result -> {
                    if (!result) {
                        log.warn("delete role fail,role {} is not exists.", id);
                        return Mono.error(NotFoundException.create("role is not exists."));
                    }

                    return assignmentRepository.existsByRoleId(id);
                })
                .flatMap(exists -> {
                    if (exists) {
                        log.warn("delete role fail,role {} is used.", id);
                        return Mono.error(UsedException.create("role is used."));
                    }

                    return rolePermissionRepository.deleteByRoleId(id).flatMap(k -> roleRepository.deleteById(id))
                            .map(roleResult -> {
                                log.info("delete role result:{}", roleResult);
                                return roleResult > 0;
                            });
                })
                ;
    }
}

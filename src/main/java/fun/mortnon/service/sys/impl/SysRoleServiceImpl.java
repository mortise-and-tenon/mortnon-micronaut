package fun.mortnon.service.sys.impl;

import fun.mortnon.dal.sys.entity.SysPermission;
import fun.mortnon.dal.sys.entity.SysRole;
import fun.mortnon.dal.sys.entity.SysRolePermission;
import fun.mortnon.dal.sys.entity.SysUser;
import fun.mortnon.dal.sys.repository.AssignmentRepository;
import fun.mortnon.dal.sys.repository.PermissionRepository;
import fun.mortnon.dal.sys.repository.RolePermissionRepository;
import fun.mortnon.dal.sys.repository.RoleRepository;
import fun.mortnon.dal.sys.specification.Specifications;
import fun.mortnon.framework.enums.ErrorCodeEnum;
import fun.mortnon.framework.exceptions.NotFoundException;
import fun.mortnon.framework.exceptions.ParameterException;
import fun.mortnon.framework.exceptions.RepeatDataException;
import fun.mortnon.framework.exceptions.UsedException;
import fun.mortnon.service.sys.SysRoleService;
import fun.mortnon.service.sys.vo.SysPermissionDTO;
import fun.mortnon.service.sys.vo.SysRoleDTO;
import fun.mortnon.web.controller.role.command.CreateRoleCommand;
import fun.mortnon.web.controller.role.command.RolePageSearch;
import fun.mortnon.web.controller.role.command.UpdateRoleCommand;
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

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.micronaut.data.repository.jpa.criteria.PredicateSpecification.where;

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
        return roleRepository.existsByNameOrIdentifier(createRoleCommand.getName(), createRoleCommand.getIdentifier())
                .flatMap(result -> {
                    if (result) {
                        log.warn("Failed to create role, duplicate data is {} and {}.", createRoleCommand.getName(), createRoleCommand.getIdentifier());
                        return Mono.error(RepeatDataException.create(ErrorCodeEnum.ROLE_NAME_REPEAT));
                    }

                    SysRole sysRole = new SysRole();
                    sysRole.setName(createRoleCommand.getName());
                    sysRole.setIdentifier(createRoleCommand.getIdentifier());
                    sysRole.setDescription(createRoleCommand.getDescription());
                    sysRole.setStatus(createRoleCommand.isStatus());

                    return roleRepository.save(sysRole);
                })
                .flatMap(role -> {
                    if(CollectionUtils.isEmpty(createRoleCommand.getPermissions())){
                        return Mono.just(SysRoleDTO.convert(role));
                    }
                    return permissionRepository.findByIdIn(createRoleCommand.getPermissions())
                            .flatMap(permission -> {
                                if (ObjectUtils.isEmpty(permission.getDependency())) {
                                    return Mono.just(new HashSet<Long>());
                                }
                                return permissionRepository.findByIdentifierIn(Arrays.asList(permission.getDependency().split(",")))
                                        .map(SysPermission::getId)
                                        .collect(Collectors.toSet());
                            })
                            .collectList()
                            .map(list -> {
                                Set<Long> set = new HashSet<>();
                                list.forEach(item -> set.addAll(item));
                                return set;
                            })
                            .map(extendSet -> {
                                Set<Long> permissionSet = createRoleCommand.getPermissions().stream().collect(Collectors.toSet());
                                permissionSet.addAll(extendSet);
                                return permissionSet;
                            })
                            .map(permissionSet -> {
                                List<SysRolePermission> rolePermissionList = new ArrayList<>();
                                permissionSet.forEach(k -> {
                                    SysRolePermission rolePermission = new SysRolePermission();
                                    rolePermission.setRoleId(role.getId());
                                    rolePermission.setPermissionId(k);
                                    rolePermissionList.add(rolePermission);
                                });
                                return rolePermissionList;
                            })
                            .flatMap(rolePermissionList -> {
                                SysRoleDTO roleData = SysRoleDTO.convert(role);

                                return rolePermissionRepository.saveAll(rolePermissionList)
                                        .collectList()
                                        .flatMapMany(list ->
                                                permissionRepository.findByIdIn(createRoleCommand.getPermissions())
                                        ).collectList()
                                        .map(list -> {
                                            if (list.size() != rolePermissionList.size()) {
                                                log.warn("Failed to create role, permission data error.");
                                                return Mono.error(ParameterException.create(ErrorCodeEnum.PERMISSION_ERROR));
                                            }
                                            List<SysPermissionDTO> pList = list.stream().map(k -> SysPermissionDTO.convert(k))
                                                    .collect(Collectors.toList());
                                            roleData.setPermissions(pList);
                                            return roleData;
                                        }).then(Mono.just(roleData));
                            });
                });

    }

    @Override
    @Transactional
    public Mono<SysRoleDTO> updateRole(UpdateRoleCommand updateRoleCommand) {
        return roleRepository.existsById(updateRoleCommand.getId())
                .flatMap(exists -> {
                    if (!exists) {
                        log.warn("Failed to update role, role id {} does not exist.", updateRoleCommand.getId());
                        return Mono.error(NotFoundException.create(ErrorCodeEnum.ROLE_NOT_EXIST));
                    }
                    return roleRepository.existsByIdNotEqualsAndNameEquals(updateRoleCommand.getId(), updateRoleCommand.getName());
                })
                .flatMap(result -> {
                    if (result) {
                        log.info("The role name [{}] of role id {} duplicates.", updateRoleCommand.getName(), updateRoleCommand.getId());
                        return Mono.error(RepeatDataException.create(ErrorCodeEnum.ROLE_NAME_REPEAT));
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

                    if (ObjectUtils.isNotEmpty(updateRoleCommand.getStatus())) {
                        role.setStatus(updateRoleCommand.getStatus());
                    }

                    SysRoleDTO roleData = SysRoleDTO.convert(role);

                    return roleRepository.update(role)
                            .flatMap(newRole ->
                                    rolePermissionRepository.deleteByRoleId(updateRoleCommand.getId())
                            )
                            .flatMapMany(result -> {
                                return permissionRepository.findByIdIn(updateRoleCommand.getPermissions())
                                        .flatMap(permission -> {
                                            if (ObjectUtils.isEmpty(permission.getDependency())) {
                                                return Mono.just(new HashSet<Long>());
                                            }
                                            return permissionRepository.findByIdentifierIn(Arrays.asList(permission.getDependency().split(",")))
                                                    .map(SysPermission::getId)
                                                    .collect(Collectors.toSet());
                                        })
                                        .collectList()
                                        .map(list -> {
                                            Set<Long> set = new HashSet<>();
                                            list.forEach(item -> set.addAll(item));
                                            return set;
                                        })
                                        .map(extendSet -> {
                                            Set<Long> permissionSet = updateRoleCommand.getPermissions().stream().collect(Collectors.toSet());
                                            permissionSet.addAll(extendSet);
                                            return permissionSet;
                                        })
                                        .map(permissionSet -> {
                                            List<SysRolePermission> rolePermissionList = new ArrayList<>();
                                            permissionSet.forEach(k -> {
                                                SysRolePermission rolePermission = new SysRolePermission();
                                                rolePermission.setRoleId(role.getId());
                                                rolePermission.setPermissionId(k);
                                                rolePermissionList.add(rolePermission);
                                            });
                                            return rolePermissionList;
                                        })
                                        .flatMapMany(pList -> rolePermissionRepository.saveAll(pList));
                            })
                            .collectList()
                            .flatMapMany(rpList ->
                                    permissionRepository.findByIdIn(updateRoleCommand.getPermissions())
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
    public Mono<Page<SysRoleDTO>> queryRoles(RolePageSearch pageSearch) {
        //默认按id正序排列
        Pageable pageable = pageSearch.convert();
        if (!pageable.isSorted()) {
            pageable = pageable.order(Sort.Order.asc("id"));
        }

        return roleRepository.findAll(where(queryCondition(pageSearch)), pageable).flatMap(page ->
                //将分页数据中的原始角色信息转换为 DTO，并循环角色 DTO 进行数据处理
                Flux.fromStream(page.getContent().stream().map(SysRoleDTO::convert))
                        .flatMapSequential(role ->
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

    /**
     * 组合查询条件
     *
     * @param pageSearch
     * @return
     */
    private PredicateSpecification<SysRole> queryCondition(RolePageSearch pageSearch) {
        PredicateSpecification<SysRole> query = null;

        if (StringUtils.isNotEmpty(pageSearch.getName())) {
            query = Specifications.propertyLike("name", pageSearch.getName());
        }

        if (StringUtils.isNotEmpty(pageSearch.getIdentifier())) {
            PredicateSpecification<SysRole> subQuery = Specifications.propertyLike("identifier", pageSearch.getIdentifier());
            if (query == null) {
                query = subQuery;
            } else {
                query = query.and(subQuery);
            }
        }

        if (ObjectUtils.isNotEmpty(pageSearch.getStatus())) {
            PredicateSpecification<SysRole> subQuery = Specifications.propertyEqual("status", pageSearch.getStatus());
            if (query == null) {
                query = subQuery;
            } else {
                query = query.and(subQuery);
            }
        }


        return query;
    }

    @Override
    public Mono<SysRoleDTO> queryRole(Long id) {
        return roleRepository.existsById(id)
                .flatMap(result -> {
                    if (!result) {
                        log.warn("Failed to query role, role id {} does not exist.", id);
                        return Mono.error(NotFoundException.create(ErrorCodeEnum.ROLE_NOT_EXIST));
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
        if (id == 1) {
            log.warn("Default roles are prohibited from being deleted.");
            return Mono.error(ParameterException.create(ErrorCodeEnum.DEFAULT_ROLE_FORBID_DELETE));
        }

        return roleRepository.existsById(id)
                .flatMap(result -> {
                    if (!result) {
                        log.warn("Failed to delete role, role id {} does not exist.", id);
                        return Mono.error(NotFoundException.create(ErrorCodeEnum.ROLE_NOT_EXIST));
                    }

                    return assignmentRepository.existsByRoleId(id);
                })
                .flatMap(exists -> {
                    if (exists) {
                        log.warn("Failed to delete role, role id {} has already been used.", id);
                        return Mono.error(UsedException.create(ErrorCodeEnum.ROLE_USED));
                    }

                    return rolePermissionRepository.deleteByRoleId(id)
                            .flatMap(k -> roleRepository.deleteById(id))
                            .map(roleResult -> roleResult > 0);
                })
                ;
    }
}

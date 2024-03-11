package fun.mortnon.service.sys.impl;

import fun.mortnon.dal.sys.entity.SysAssignment;
import fun.mortnon.dal.sys.entity.SysPermission;
import fun.mortnon.dal.sys.entity.SysRole;
import fun.mortnon.dal.sys.repository.AssignmentRepository;
import fun.mortnon.dal.sys.repository.PermissionRepository;
import fun.mortnon.dal.sys.repository.RolePermissionRepository;
import fun.mortnon.dal.sys.repository.RoleRepository;
import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.service.sys.AssignmentService;
import fun.mortnon.service.sys.ProfileService;
import fun.mortnon.service.sys.SysMenuService;
import fun.mortnon.service.sys.SysUserService;
import fun.mortnon.service.sys.vo.ProfileDTO;
import fun.mortnon.service.sys.vo.SysUserDTO;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import reactor.core.publisher.Mono;

import java.security.Principal;

/**
 * @author dev2007
 * @date 2024/3/11
 */
@Singleton
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    @Inject
    private SysUserService sysUserService;

    @Inject
    private SysMenuService sysMenuService;

    @Inject
    private AssignmentRepository assignmentRepository;

    @Inject
    private RoleRepository roleRepository;

    @Inject
    private RolePermissionRepository rolePermissionRepository;

    @Inject
    private PermissionRepository permissionRepository;

    @Override
    public Mono<ProfileDTO> queryProfile(@Nullable Principal principal) {
        ProfileDTO profileDTO = new ProfileDTO();

        return sysUserService.getUserByUsername(principal.getName())
                .flatMap(user -> {
                    profileDTO.setUser(SysUserDTO.convert(user));
                    return assignmentRepository.findByUserId(user.getId())
                            .collectList()
                            .map(list -> list.size() > 0 ? list.get(0) : new SysAssignment());
                })
                .flatMap(assignment -> {
                    if (ObjectUtils.isNotEmpty(assignment.getRoleId())) {
                        return roleRepository.findById(assignment.getRoleId());
                    }
                    return Mono.empty();
                })
                .flatMap(role ->
                        rolePermissionRepository.findByRoleId(role.getId())
                                .flatMap(rolePermission ->
                                        permissionRepository.findById(rolePermission.getPermissionId())
                                                .map(SysPermission::getIdentifier)
                                )
                                .collectList()
                )
                .map(permissionList -> {
                    profileDTO.setPermission(permissionList);
                    return profileDTO;
                })
                .flatMap(profile ->
                        sysMenuService.queryUserMenu(principal)
                                .map(menus -> {
                                    profile.setMenu(menus);
                                    return profile;
                                })
                );

    }
}

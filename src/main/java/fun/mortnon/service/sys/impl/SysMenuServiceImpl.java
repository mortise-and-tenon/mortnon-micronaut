package fun.mortnon.service.sys.impl;

import fun.mortnon.dal.sys.entity.SysMenu;
import fun.mortnon.dal.sys.entity.SysPermission;
import fun.mortnon.dal.sys.entity.SysRolePermission;
import fun.mortnon.dal.sys.repository.MenuRepository;
import fun.mortnon.dal.sys.repository.PermissionRepository;
import fun.mortnon.dal.sys.repository.RolePermissionRepository;
import fun.mortnon.dal.sys.repository.RoleRepository;
import fun.mortnon.dal.sys.specification.Specifications;
import fun.mortnon.framework.enums.ErrorCodeEnum;
import fun.mortnon.framework.exceptions.ParameterException;
import fun.mortnon.service.sys.SysMenuService;
import fun.mortnon.service.sys.vo.SysMenuDTO;
import fun.mortnon.service.sys.vo.SysMenuTreeDTO;
import fun.mortnon.web.controller.menu.command.CreateMenuCommand;
import fun.mortnon.web.controller.menu.command.MenuSearch;
import fun.mortnon.web.controller.menu.command.UpdateMenuCommand;
import io.micronaut.core.util.StringUtils;
import io.micronaut.data.repository.jpa.criteria.PredicateSpecification;
import io.micronaut.security.authentication.ServerAuthentication;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.micronaut.data.repository.jpa.criteria.PredicateSpecification.where;

/**
 * 菜单服务
 *
 * @author dev2007
 * @date 2023/12/5
 */
@Singleton
@Slf4j
public class SysMenuServiceImpl implements SysMenuService {
    @Inject
    private MenuRepository menuRepository;

    @Inject
    private PermissionRepository permissionRepository;

    @Inject
    private RolePermissionRepository rolePermissionRepository;

    @Inject
    private RoleRepository roleRepository;

    @Override
    public Mono<List<SysMenuTreeDTO>> queryMenu(MenuSearch pageSearch) {
        return menuRepository.findAll(where(queryCondition(pageSearch)))
                .collectList()
                .map(menuList -> convertTree(menuList));
    }

    /**
     * 转换树形
     *
     * @param menuList
     * @return
     */
    private List<SysMenuTreeDTO> convertTree(List<SysMenu> menuList) {
        List<SysMenuTreeDTO> tree = new ArrayList<>();
        menuList.forEach(node -> {
            boolean result = bindToParent(tree, node);
            if (!result) {
                tree.add(SysMenuTreeDTO.convert(node));
            }
        });
        return tree;
    }

    private boolean bindToParent(List<SysMenuTreeDTO> list, SysMenu node) {
        for (SysMenuTreeDTO current : list) {
            if (current.getId() == node.getParentId()) {
                current.getChildren().add(SysMenuTreeDTO.convert(node));
                return true;
            }
            if (current.getChildren().size() > 0) {
                return bindToParent(current.getChildren(), node);
            }
        }
        return false;
    }

    /**
     * 组合过滤条件
     *
     * @param search
     * @return
     */
    private PredicateSpecification<SysMenu> queryCondition(MenuSearch search) {
        PredicateSpecification<SysMenu> query = null;

        if (org.apache.commons.lang3.StringUtils.isNotEmpty(search.getName())) {
            query = Specifications.propertyLike("name", search.getName());
        }

        if (ObjectUtils.isNotEmpty(search.getStatus())) {
            PredicateSpecification<SysMenu> subQuery = Specifications.propertyEqual("status", search.getStatus());
            if (query == null) {
                query = subQuery;
            } else {
                query = query.and(subQuery);
            }
        }

        return query;
    }

    @Override
    public Mono<SysMenuDTO> queryMenuById(Long id) {
        return menuRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(ParameterException.create("result.menu.id.not.exists.fail"));
                    }

                    return menuRepository.findById(id);
                })
                .map(SysMenuDTO::convert);
    }

    @Override
    public Mono<SysMenuDTO> createMenu(CreateMenuCommand createMenuCommand) {
        return permissionRepository.existsByIdentifier(createMenuCommand.getPermission())
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(ParameterException.create(ErrorCodeEnum.PERMISSION_ERROR));
                    }
                    SysMenu sysMenu = new SysMenu();
                    sysMenu.setName(createMenuCommand.getName());
                    sysMenu.setOrder(createMenuCommand.getOrder());
                    sysMenu.setUrl(createMenuCommand.getUrl());
                    sysMenu.setIcon(createMenuCommand.getIcon());
                    sysMenu.setParentId(createMenuCommand.getParentId());
                    sysMenu.setPermission(createMenuCommand.getPermission());
                    boolean status = ObjectUtils.isEmpty(createMenuCommand.getStatus()) ? true : createMenuCommand.getStatus();
                    sysMenu.setStatus(status);

                    return menuRepository.save(sysMenu);
                })
                .map(SysMenuDTO::convert);
    }

    @Override
    public Mono<Boolean> deleteMenu(Long id) {
        return menuRepository.existsById(id)
                .flatMap(exists -> {
                    //菜单不存在，幂等，直接响应成功
                    if (!exists) {
                        log.warn("delete menu fail,menu id [{}] is not exists.", id);
                        return Mono.just(1L);
                    }
                    return menuRepository.deleteById(id);
                })
                .map(result -> result > 0);
    }

    @Override
    public Mono<SysMenuDTO> updateMenu(UpdateMenuCommand updateMenuCommand) {
        return menuRepository.existsById(updateMenuCommand.getId())
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(ParameterException.create("menu id is not exists."));
                    }

                    return menuRepository.findById(updateMenuCommand.getId())
                            .flatMap(sysMenu -> {
                                if (ObjectUtils.isNotEmpty(updateMenuCommand.getParentId())) {
                                    sysMenu.setParentId(updateMenuCommand.getParentId());
                                }
                                if (StringUtils.isNotEmpty(updateMenuCommand.getName())) {
                                    sysMenu.setName(updateMenuCommand.getName());
                                }
                                if (ObjectUtils.isNotEmpty(updateMenuCommand.getOrder())) {
                                    sysMenu.setOrder(updateMenuCommand.getOrder());
                                }
                                if (StringUtils.isNotEmpty(updateMenuCommand.getUrl())) {
                                    sysMenu.setUrl(updateMenuCommand.getUrl());
                                }
                                if (StringUtils.isNotEmpty((updateMenuCommand.getIcon()))) {
                                    sysMenu.setIcon(updateMenuCommand.getIcon());
                                }
                                if (StringUtils.isNotEmpty(updateMenuCommand.getPermission())) {
                                    sysMenu.setPermission(updateMenuCommand.getPermission());
                                }
                                if (ObjectUtils.isNotEmpty(updateMenuCommand.getStatus())) {
                                    sysMenu.setStatus(updateMenuCommand.getStatus());
                                }

                                return menuRepository.update(sysMenu);
                            });
                })
                .map(SysMenuDTO::convert);
    }

    @Override
    public Mono<List<SysMenuTreeDTO>> queryUserMenu(Principal principal) {
        if (!(principal instanceof ServerAuthentication)) {
            return Mono.just(Collections.EMPTY_LIST);
        }

        ServerAuthentication userPrincipal = (ServerAuthentication) principal;
        List<String> roles = (ArrayList<String>) userPrincipal.getAttributes().getOrDefault("roles", "");
        return roleRepository.findByIdentifier(roles.get(0))
                .flatMap(role ->
                        rolePermissionRepository.findByRoleId(role.getId())
                                .map(SysRolePermission::getPermissionId)
                                .flatMap(pId -> permissionRepository.findById(pId))
                                .collectList()
                ).flatMap(permissionList -> {
                    List<String> identifierList = permissionList.stream().map(SysPermission::getIdentifier)
                            .collect(Collectors.toList());
                    return menuRepository.findAll().collectList()
                            .map(list -> list.stream().filter(menu -> identifierList.contains(menu.getPermission())
                                            || StringUtils.isEmpty(menu.getPermission()))
                                    .collect(Collectors.toList()));
                })
                .map(menuList -> convertTree(menuList));
    }

}

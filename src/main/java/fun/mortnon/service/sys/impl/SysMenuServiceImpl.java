package fun.mortnon.service.sys.impl;

import fun.mortnon.dal.sys.entity.SysMenu;
import fun.mortnon.dal.sys.entity.SysPermission;
import fun.mortnon.dal.sys.entity.SysRolePermission;
import fun.mortnon.dal.sys.entity.config.MenuType;
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
import java.util.*;
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
        List<SysMenuTreeDTO> nodeList = menuList.stream().map(SysMenuTreeDTO::convert).collect(Collectors.toList());

        for (SysMenuTreeDTO node : nodeList) {
            SysMenuTreeDTO parent = nodeList.stream().filter(parentNode -> parentNode.getId().equals(node.getParentId()))
                    .findAny().orElse(null);
            if (Objects.nonNull(parent)) {
                parent.getChildren().add(node);
            }
        }

        List<SysMenuTreeDTO> tempTree = nodeList.stream()
                .filter(node -> node.getParentId() == 0L || !node.getChildren().isEmpty()).collect(Collectors.toList());

        List<SysMenuTreeDTO> treeList = tempTree.stream().filter(node -> tempTree.stream().noneMatch(k -> k.getId().equals(node.getParentId())))
                .collect(Collectors.toList());


        return treeList;
    }

    /**
     * 转换当前用户菜单
     * 要排除用户无权限菜单后的空父级菜单
     *
     * @param menuList
     * @return
     */
    private List<SysMenuTreeDTO> convertUserTree(List<SysMenu> menuList) {
        List<SysMenuTreeDTO> treeList = convertTree(menuList);
        // 递归移除空 children 节点
        // 递归移除空 children 节点
        treeList.removeIf(node -> removeEmptyChildren(node));
        return treeList;
    }

    private boolean removeEmptyChildren(SysMenuTreeDTO node) {
        if (node.getChildren().isEmpty() && node.getType().equals(MenuType.GROUP)) {
            return true;
        }
        node.getChildren().removeIf(this::removeEmptyChildren);
        return node.getChildren().isEmpty() && node.getType().equals(MenuType.GROUP);
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
                        return Mono.error(ParameterException.create("result.menu.id.not.exist.fail"));
                    }

                    return menuRepository.findById(id);
                })
                .map(SysMenuDTO::convert);
    }

    @Override
    public Mono<SysMenuDTO> createMenu(CreateMenuCommand createMenuCommand) {
        return Mono.just(createMenuCommand)
                .map(command -> {
                    SysMenu sysMenu = new SysMenu();
                    sysMenu.setName(createMenuCommand.getName());
                    sysMenu.setOrder(createMenuCommand.getOrder());
                    sysMenu.setUrl(createMenuCommand.getUrl());
                    sysMenu.setIcon(createMenuCommand.getIcon());
                    sysMenu.setParentId(createMenuCommand.getParentId());
                    boolean status = ObjectUtils.isEmpty(createMenuCommand.getStatus()) ? true : createMenuCommand.getStatus();
                    sysMenu.setStatus(status);
                    return sysMenu;
                })
                .flatMap(sysMenu -> {
                    if (StringUtils.isNotEmpty(createMenuCommand.getPermission())) {
                        return permissionRepository.existsByIdentifier(createMenuCommand.getPermission())
                                .flatMap(exists -> {
                                    if (!exists) {
                                        return Mono.error(ParameterException.create(ErrorCodeEnum.PERMISSION_ERROR));
                                    }
                                    sysMenu.setPermission(createMenuCommand.getPermission());
                                    return Mono.just(sysMenu);
                                });
                    }
                    return Mono.just(sysMenu);
                })
                .flatMap(sysMenu -> menuRepository.save(sysMenu))
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
                            .map(list -> list.stream().filter(menu -> {
                                        if (StringUtils.isNotEmpty(menu.getPermission())) {
                                            List<String> menuPermissionList = Arrays.asList(menu.getPermission().split(","));
                                            return identifierList.containsAll(menuPermissionList)
                                                    || StringUtils.isEmpty(menu.getPermission());
                                        }
                                        return true;
                                    })
                                    .collect(Collectors.toList()));
                })
                .map(this::convertUserTree);
    }

}

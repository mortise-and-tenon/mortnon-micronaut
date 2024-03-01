package fun.mortnon.service.sys.impl;

import fun.mortnon.dal.sys.entity.SysMenu;
import fun.mortnon.dal.sys.entity.SysPermission;
import fun.mortnon.dal.sys.entity.SysProject;
import fun.mortnon.dal.sys.entity.SysRolePermission;
import fun.mortnon.dal.sys.repository.MenuRepository;
import fun.mortnon.dal.sys.repository.PermissionRepository;
import fun.mortnon.dal.sys.repository.RolePermissionRepository;
import fun.mortnon.dal.sys.repository.RoleRepository;
import fun.mortnon.dal.sys.specification.Specifications;
import fun.mortnon.framework.exceptions.NotFoundException;
import fun.mortnon.framework.exceptions.ParameterException;
import fun.mortnon.framework.exceptions.RepeatDataException;
import fun.mortnon.framework.exceptions.UsedException;
import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.service.sys.SysMenuService;
import fun.mortnon.service.sys.vo.SysMenuDTO;
import fun.mortnon.service.sys.vo.SysProjectTreeDTO;
import fun.mortnon.web.controller.menu.command.CreateMenuCommand;
import fun.mortnon.web.controller.menu.command.MenuPageSearch;
import fun.mortnon.web.controller.menu.command.UpdateMenuCommand;
import fun.mortnon.web.controller.project.command.ProjectPageSearch;
import fun.mortnon.web.controller.project.command.UpdateProjectCommand;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.util.StringUtils;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.jpa.criteria.PredicateSpecification;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.authentication.ServerAuthentication;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.micronaut.data.repository.jpa.criteria.PredicateSpecification.where;

/**
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
    public Mono<List<SysMenuDTO>> queryMenu(MenuPageSearch pageSearch) {
        return menuRepository.findAll(where(queryCondition(pageSearch)))
                .collectList().map(menuList -> convertTree(menuList));
    }

    private List<SysMenuDTO> convertTree(List<SysMenu> menuList) {
        List<SysMenuDTO> tree = new ArrayList<>();
        menuList.forEach(node -> {
            boolean result = bindToParent(tree, node);
            if (!result) {
                tree.add(SysMenuDTO.convert(node));
            }
        });
        return tree;
    }

    private PredicateSpecification<SysMenu> queryCondition(MenuPageSearch search) {
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
        return menuRepository.findById(id).map(SysMenuDTO::convert);
    }

    private boolean bindToParent(List<SysMenuDTO> list, SysMenu node) {
        for (SysMenuDTO current : list) {
            if (current.getId() == node.getParentId()) {
                current.getChildren().add(SysMenuDTO.convert(node));
                return true;
            }
            if (current.getChildren().size() > 0) {
                return bindToParent(current.getChildren(), node);
            }
        }
        return false;
    }

    @Override
    public Mono<SysMenu> createMenu(CreateMenuCommand createMenuCommand) {
        return permissionRepository.existsByIdentifier(createMenuCommand.getPermission())
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(ParameterException.create("permission is incorrect."));
                    }
                    SysMenu sysMenu = new SysMenu();
                    sysMenu.setName(createMenuCommand.getName());
                    sysMenu.setOrder(createMenuCommand.getOrder());
                    sysMenu.setUrl(createMenuCommand.getUrl());
                    sysMenu.setIcon(createMenuCommand.getIcon());
                    sysMenu.setParentId(createMenuCommand.getParentId());
                    sysMenu.setPermission(createMenuCommand.getPermission());
                    sysMenu.setStatus((createMenuCommand.isStatus()));

                    return menuRepository.save(sysMenu);
                });
    }

    @Override
    public Mono<Boolean> deleteMenu(Long id) {
        if (null == id || id <= 0) {
            return Mono.error(ParameterException.create("menu id is not exists."));
        }

        return menuRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        log.warn("delete menu fail,menu id [{}] is not exists.", id);
                        return Mono.error(NotFoundException.create("menu id is not exists."));
                    }
                    return menuRepository.deleteById(id);
                })
                .map(result -> result > 0);
    }

    @Override
    public Mono<SysMenu> updateMenu(UpdateMenuCommand updateMenuCommand) {
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

                                sysMenu.setStatus(updateMenuCommand.isStatus());

                                return menuRepository.update(sysMenu);
                            });
                });
    }

    @Override
    public Mono<List<SysMenuDTO>> queryUserMenu(Principal principal) {
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

package fun.mortnon.service.sys.impl;

import fun.mortnon.dal.sys.entity.SysMenu;
import fun.mortnon.dal.sys.repository.MenuRepository;
import fun.mortnon.dal.sys.repository.PermissionRepository;
import fun.mortnon.framework.exceptions.NotFoundException;
import fun.mortnon.framework.exceptions.ParameterException;
import fun.mortnon.framework.exceptions.RepeatDataException;
import fun.mortnon.framework.exceptions.UsedException;
import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.service.sys.SysMenuService;
import fun.mortnon.service.sys.vo.SysMenuDTO;
import fun.mortnon.web.controller.menu.command.CreateMenuCommand;
import fun.mortnon.web.controller.menu.command.UpdateMenuCommand;
import fun.mortnon.web.controller.project.command.UpdateProjectCommand;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.util.StringUtils;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Put;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public Mono<List<SysMenuDTO>> queryMenu() {
        return menuRepository.findAll().collectList().map(menuList -> paresMenu(menuList, 0L));
    }

    private List<SysMenuDTO> paresMenu(List<SysMenu> menuList, Long menuId) {
        List<SysMenuDTO> levelCurrentMenuList = menuList.stream().filter(menu -> menu.getParentId().equals(menuId))
                .map(SysMenuDTO::convert)
                .collect(Collectors.toList());

        levelCurrentMenuList.forEach(levelCurrentMenu -> {
            List<SysMenuDTO> childMenuList = paresMenu(menuList, levelCurrentMenu.getId());
            levelCurrentMenu.setChildrenMenu(childMenuList);
        });

        return levelCurrentMenuList;
    }

    @Override
    public Mono<SysMenu> createMenu(CreateMenuCommand createMenuCommand) {
        return permissionRepository.existsByIdentifierContains(createMenuCommand.getPermission())
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
                    sysMenu.setPermission(String.join(",", createMenuCommand.getPermission()));

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
                                if (CollectionUtils.isNotEmpty(updateMenuCommand.getPermission())) {
                                    sysMenu.setPermission(String.join(",", updateMenuCommand.getPermission()));
                                }

                                return menuRepository.update(sysMenu);
                            });
                });
    }

}

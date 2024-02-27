package fun.mortnon.service.sys;

import fun.mortnon.dal.sys.entity.SysMenu;
import fun.mortnon.service.sys.vo.SysMenuDTO;
import fun.mortnon.service.sys.vo.SysPermissionDTO;
import fun.mortnon.service.sys.vo.SysProjectDTO;
import fun.mortnon.web.controller.menu.command.CreateMenuCommand;
import fun.mortnon.web.controller.menu.command.UpdateMenuCommand;
import fun.mortnon.web.controller.project.command.UpdateProjectCommand;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.List;

/**
 * @author dev2007
 * @date 2023/12/5
 */
public interface SysMenuService {
    Mono<List<SysMenuDTO>> queryMenu();

    Mono<SysMenuDTO> queryMenuById(Long id);

    Mono<SysMenu> createMenu(CreateMenuCommand createMenuCommand);

    Mono<Boolean> deleteMenu(Long id);

    Mono<SysMenu> updateMenu(UpdateMenuCommand updateMenuCommand);

    Mono<List<SysMenuDTO>> queryUserMenu(Principal principal);
}

package fun.mortnon.service.sys;

import fun.mortnon.service.sys.vo.SysMenuDTO;
import fun.mortnon.service.sys.vo.SysMenuTreeDTO;
import fun.mortnon.web.controller.menu.command.CreateMenuCommand;
import fun.mortnon.web.controller.menu.command.MenuSearch;
import fun.mortnon.web.controller.menu.command.UpdateMenuCommand;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.List;

/**
 * @author dev2007
 * @date 2023/12/5
 */
public interface SysMenuService {
    /**
     * 查询树形菜单
     *
     * @param pageSearch
     * @return
     */
    Mono<List<SysMenuTreeDTO>> queryMenu(MenuSearch pageSearch);

    /**
     * 查询指定菜单
     *
     * @param id 菜单 id
     * @return
     */
    Mono<SysMenuDTO> queryMenuById(Long id);

    /**
     * 创建菜单
     *
     * @param createMenuCommand
     * @return
     */
    Mono<SysMenuDTO> createMenu(CreateMenuCommand createMenuCommand);

    /**
     * 删除指定菜单
     *
     * @param id
     * @return
     */
    Mono<Boolean> deleteMenu(Long id);

    /**
     * 更新指定菜单
     *
     * @param updateMenuCommand
     * @return
     */
    Mono<SysMenuDTO> updateMenu(UpdateMenuCommand updateMenuCommand);

    /**
     * 查询登录用户的菜单
     *
     * @param principal 认证凭证
     * @return
     */
    Mono<List<SysMenuTreeDTO>> queryUserMenu(Principal principal);
}

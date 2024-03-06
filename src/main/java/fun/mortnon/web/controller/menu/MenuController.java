package fun.mortnon.web.controller.menu;

import fun.mortnon.framework.aop.OperationLog;
import fun.mortnon.framework.constants.LogConstants;
import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.service.sys.SysMenuService;
import fun.mortnon.service.sys.vo.SysMenuDTO;
import fun.mortnon.service.sys.vo.SysMenuTreeDTO;
import fun.mortnon.web.controller.menu.command.CreateMenuCommand;
import fun.mortnon.web.controller.menu.command.MenuSearch;
import fun.mortnon.web.controller.menu.command.UpdateMenuCommand;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * 菜单
 *
 * @author dev2007
 * @date 2023/12/5
 */
@Controller("/menus")
@Slf4j
public class MenuController {
    /**
     * 菜单服务
     */
    @Inject
    private SysMenuService sysMenuService;

    /**
     * 查询树形菜单
     *
     * @param pageSearch 查询参数
     * @return
     */
    @Get("{?pageSearch*}")
    public Mono<MortnonResult<List<SysMenuTreeDTO>>> queryMenuTree(MenuSearch pageSearch) {
        return sysMenuService.queryMenu(pageSearch)
                .map(MortnonResult::success);
    }

    /**
     * 查询指定菜单
     *
     * @param id 菜单 id
     * @return
     */
    @Get("/{id}")
    public Mono<MortnonResult<SysMenuDTO>> queryMenu(@NotNull @Positive Long id) {
        return sysMenuService.queryMenuById(id)
                .map(MortnonResult::success);
    }

    /**
     * 创建新菜单
     *
     * @param createMenuCommand
     * @return
     */
    @OperationLog(LogConstants.MENU_CREATE)
    @Post
    public Mono<MutableHttpResponse<MortnonResult<SysMenuDTO>>> createMenu(@NonNull CreateMenuCommand createMenuCommand) {
        return sysMenuService.createMenu(createMenuCommand)
                .map(MortnonResult::success)
                .map(HttpResponse::created);
    }

    /**
     * 删除指定菜单
     *
     * @param id 菜单 id
     * @return
     */
    @OperationLog(LogConstants.MENU_DELETE)
    @Delete("/{id}")
    public Mono<MutableHttpResponse<MortnonResult>> deleteMenu(@NotNull @Positive Long id) {
        return sysMenuService.deleteMenu(id)
                .map(MortnonResult::success)
                .map(HttpResponse::ok);
    }

    /**
     * 修改指定菜单
     *
     * @param updateMenuCommand 更新的菜单数据
     * @return
     */
    @OperationLog(LogConstants.MENU_UPDATE)
    @Put
    public Mono<MutableHttpResponse<MortnonResult<SysMenuDTO>>> updateMenu(@NonNull UpdateMenuCommand updateMenuCommand) {
        return sysMenuService.updateMenu(updateMenuCommand)
                .map(MortnonResult::success)
                .map(HttpResponse::ok);
    }
}

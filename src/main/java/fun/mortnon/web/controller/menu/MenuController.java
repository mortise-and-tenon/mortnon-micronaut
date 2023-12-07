package fun.mortnon.web.controller.menu;

import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.service.sys.SysMenuService;
import fun.mortnon.service.sys.vo.SysMenuDTO;
import fun.mortnon.web.controller.menu.command.CreateMenuCommand;
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
 * @author dev2007
 * @date 2023/12/5
 */
@Controller("/menus")
@Slf4j
public class MenuController {
    @Inject
    private SysMenuService sysMenuService;

    /**
     * 查询所有菜单项
     *
     * @return
     */
    @Get
    public Mono<MortnonResult<List<SysMenuDTO>>> queryMenu() {
        return sysMenuService.queryMenu().map(MortnonResult::success);
    }

    /**
     * 创建新菜单
     *
     * @param createMenuCommand
     * @return
     */
    @Post
    public Mono<MutableHttpResponse<MortnonResult>> createRole(@NonNull CreateMenuCommand createMenuCommand) {
        return sysMenuService.createMenu(createMenuCommand)
                .map(MortnonResult::success)
                .map(HttpResponse::created);
    }

    /**
     * 删除菜单
     *
     * @param id
     * @return
     */
    @Delete("/{id}")
    public Mono<MutableHttpResponse<MortnonResult>> deleteMenu(@NotNull @Positive Long id) {
        return sysMenuService.deleteMenu(id).map(MortnonResult::success).map(HttpResponse::ok);
    }

    /**
     * 修改菜单
     *
     * @param updateMenuCommand
     * @return
     */
    @Put
    public Mono<MutableHttpResponse<MortnonResult>> update(@NonNull UpdateMenuCommand updateMenuCommand) {
        return sysMenuService.updateMenu(updateMenuCommand).map(MortnonResult::success).map(HttpResponse::ok);
    }
}

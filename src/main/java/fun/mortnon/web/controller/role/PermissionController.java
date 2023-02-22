package fun.mortnon.web.controller.role;

import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.framework.vo.PageableData;
import fun.mortnon.service.sys.SysPermissionService;
import fun.mortnon.service.sys.vo.SysPermissionDTO;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author dev2007
 * @date 2023/2/20
 */
@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/permissions")
public class PermissionController {

    @Inject
    private SysPermissionService sysPermissionService;

    /**
     * 查询所有权限
     *
     * @param pageable
     * @return
     */
    @Get
    public Mono<MortnonResult<PageableData<List<SysPermissionDTO>>>> queryPermission(Pageable pageable) {
        return sysPermissionService.queryPermission(pageable).map(MortnonResult::successPageData);
    }
}

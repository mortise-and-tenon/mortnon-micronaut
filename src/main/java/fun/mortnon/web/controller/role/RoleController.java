package fun.mortnon.web.controller.role;

import fun.mortnon.dal.sys.entity.SysRole;
import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.framework.vo.PageableData;
import fun.mortnon.service.sys.SysRoleService;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

/**
 * @author dev2007
 * @date 2023/2/15
 */
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/roles")
@Slf4j
public class RoleController {
    @Inject
    private SysRoleService sysRoleService;
    @Get
    public Mono<MortnonResult<PageableData<List<SysRole>>>> queryUser(@Valid Pageable pageable){
        return sysRoleService.queryRoles(pageable).map(MortnonResult::success);
    }
}

package fun.mortnon.web.controller.user;

import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.framework.vo.PageableData;
import fun.mortnon.service.sys.AssignmentService;
import fun.mortnon.service.sys.vo.SysUserDTO;
import fun.mortnon.web.controller.user.command.UserPageSearch;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author dev2007
 * @date 2024/3/2
 */
@Controller("/unassignment")
public class UnassignmentController {

    @Inject
    private AssignmentService assignmentService;

    @Get("{?pageSearch*}")
    public Mono<MortnonResult<PageableData<List<SysUserDTO>>>> getAssignmentUser(UserPageSearch pageSearch) {
        return assignmentService.queryUnassignmentUser(pageSearch).map(MortnonResult::successPageData);
    }
}

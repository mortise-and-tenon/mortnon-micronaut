package fun.mortnon.web.controller.user;

import fun.mortnon.framework.aop.OperationLog;
import fun.mortnon.framework.constants.LogConstants;
import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.framework.vo.PageableData;
import fun.mortnon.service.sys.AssignmentService;
import fun.mortnon.service.sys.vo.SysUserDTO;
import fun.mortnon.web.controller.user.command.AssignmentCommand;
import fun.mortnon.web.controller.user.command.RevokeCommand;
import fun.mortnon.web.controller.user.command.UserPageSearch;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Put;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

/**
 * 分派用户角色、组织
 *
 * @author dev2007
 * @date 2023/2/27
 */
@Controller("/assignment")
public class AssignmentController {
    @Inject
    private AssignmentService assignmentService;

    /**
     * 查询指定角色分配的或未分配的用户
     *
     * @param pageSearch
     * @return
     */
    @Get("{?pageSearch*}")
    public Mono<MortnonResult<PageableData<List<SysUserDTO>>>> getAssignmentUser(UserPageSearch pageSearch) {
        return assignmentService.queryAssignmentUser(pageSearch).map(MortnonResult::successPageData);
    }

    /**
     * 分配用户组织、角色
     *
     * @param userId
     * @param projectId
     * @param roleId
     * @return
     */
    @OperationLog(LogConstants.ASSIGNMENT_CREATE)
    @Put("/user/{userId}/project/{projectId}/role/{roleId}")
    public Mono<MutableHttpResponse<MortnonResult>> assignmentUser(Long userId, Long projectId, Long roleId) {
        AssignmentCommand assignmentCommand = new AssignmentCommand(userId, projectId, roleId);
        return assignmentService.assignmentUser(assignmentCommand).map(MortnonResult::success).map(HttpResponse::ok);
    }

    /**
     * 更新/分配用户的组织、角色
     *
     * @param assignmentCommand
     * @return
     */
    @OperationLog(LogConstants.ASSIGNMENT_CREATE)
    @Put
    public Mono<MutableHttpResponse<MortnonResult>> assignmentUserWithRoleProject(@Valid @Body AssignmentCommand assignmentCommand) {
        return assignmentService.assignmentUser(assignmentCommand).map(MortnonResult::success).map(HttpResponse::ok);
    }

    /**
     * 取消用户的角色分配
     *
     * @param userId
     * @param roleId
     * @return
     */
    @OperationLog(LogConstants.ASSIGNMENT_DELETE)
    @Delete("/user/{userId}/role/{roleId}")
    public Mono<MutableHttpResponse<MortnonResult>> revokeUserWithRole(Long userId, Long roleId) {
        RevokeCommand revokeCommand = new RevokeCommand(userId, null, roleId);
        return assignmentService.revokeUser(revokeCommand).map(MortnonResult::success).map(HttpResponse::ok);
    }

    /**
     * 取消用户的组织分配
     *
     * @param userId
     * @param projectId
     * @return
     */
    @OperationLog(LogConstants.ASSIGNMENT_DELETE)
    @Delete("/user/{userId}/project/{projectId}")
    public Mono<MutableHttpResponse<MortnonResult>> revokeUserWithProject(Long userId, Long projectId) {
        RevokeCommand revokeCommand = new RevokeCommand(userId, projectId, null);
        return assignmentService.revokeUser(revokeCommand).map(MortnonResult::success).map(HttpResponse::ok);
    }

    /**
     * 撤销用户组织、角色分配
     *
     * @param userId
     * @param projectId
     * @param roleId
     * @return
     */
    @OperationLog(LogConstants.ASSIGNMENT_DELETE)
    @Delete("/user/{userId}/project/{projectId}/role/{roleId}")
    public Mono<MutableHttpResponse<MortnonResult>> revokeUser(Long userId, Long projectId, Long roleId) {
        RevokeCommand revokeCommand = new RevokeCommand(userId, projectId, roleId);
        return assignmentService.revokeUser(revokeCommand).map(MortnonResult::success).map(HttpResponse::ok);
    }
}

package fun.mortnon.web.controller.user;

import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.service.sys.AssignmentService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

/**
 * @author dev2007
 * @date 2023/2/27
 */
@Controller("/assignment")
public class AssignmentController {
    @Inject
    private AssignmentService assignmentService;

    /**
     * 分配用户组织、角色
     *
     * @param userId
     * @param projectId
     * @param roleId
     * @return
     */
    @Post("/user/{userId}/project/{projectId}/role/{roleId}")
    public Mono<MutableHttpResponse<MortnonResult>> assignmentUser(Long userId, Long projectId, Long roleId) {
        return assignmentService.assignmentUser(userId, projectId, roleId).map(MortnonResult::success).map(HttpResponse::ok);
    }

    /**
     * 撤销用户组织、角色分配
     *
     * @param userId
     * @param projectId
     * @param roleId
     * @return
     */
    @Delete("/user/{userId}/project/{projectId}/role/{roleId}")
    public Mono<MutableHttpResponse<MortnonResult>> revokeUser(Long userId, Long projectId, Long roleId) {
        return assignmentService.revokeUser(userId, projectId, roleId).map(MortnonResult::success).map(HttpResponse::ok);
    }
}

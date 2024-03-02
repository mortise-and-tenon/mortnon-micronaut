package fun.mortnon.service.sys;

import fun.mortnon.service.sys.vo.SysUserDTO;
import fun.mortnon.web.controller.user.command.RevokeCommand;
import fun.mortnon.web.controller.user.command.UserPageSearch;
import io.micronaut.data.model.Page;
import reactor.core.publisher.Mono;

/**
 * @author dev2007
 * @date 2023/2/27
 */
public interface AssignmentService {

    /**
     * 查询指定角色分配的用户列表
     *
     * @param pageSearch
     * @return
     */
    Mono<Page<SysUserDTO>> queryAssignmentUser(UserPageSearch pageSearch);

    Mono<Page<SysUserDTO>> queryUnassignmentUser(UserPageSearch pageSearch);

    /**
     * 关联用户、组织和角色
     *
     * @param userId
     * @param projectId
     * @param roleId
     * @return
     */
    Mono<Boolean> assignmentUser(Long userId, Long projectId, Long roleId);

    /**
     * 移除关联用户、组织和角色
     *
     * @param revokeCommand
     * @return
     */
    Mono<Boolean> revokeUser(RevokeCommand revokeCommand);
}

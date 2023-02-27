package fun.mortnon.service.sys;

import reactor.core.publisher.Mono;

/**
 * @author dev2007
 * @date 2023/2/27
 */
public interface AssignmentService {
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
     * @param userId
     * @param projectId
     * @param roleId
     * @return
     */
    Mono<Boolean> revokeUser(Long userId, Long projectId, Long roleId);
}

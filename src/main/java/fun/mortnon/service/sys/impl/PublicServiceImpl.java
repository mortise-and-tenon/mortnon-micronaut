package fun.mortnon.service.sys.impl;

import fun.mortnon.dal.sys.entity.SysAssignment;
import fun.mortnon.dal.sys.repository.AssignmentRepository;
import fun.mortnon.service.sys.PublicService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

/**
 * @author dev2007
 * @date 2023/2/27
 */
@Singleton
public class PublicServiceImpl implements PublicService {
    @Inject
    private AssignmentRepository assignmentRepository;

    @Override
    public Mono<SysAssignment> saveAssignment(Long userId, Long projectId, Long roleId) {
        SysAssignment sysAssignment = new SysAssignment();
        sysAssignment.setUserId(userId);
        sysAssignment.setProjectId(projectId);
        sysAssignment.setRoleId(roleId);

        return assignmentRepository.save(sysAssignment);
    }
}

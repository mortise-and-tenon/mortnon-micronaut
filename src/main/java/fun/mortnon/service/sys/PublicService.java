package fun.mortnon.service.sys;

import fun.mortnon.dal.sys.entity.SysAssignment;
import reactor.core.publisher.Mono;

/**
 * @author dev2007
 * @date 2023/2/27
 */
public interface PublicService {
    Mono<SysAssignment> saveAssignment(Long userId, Long projectId, Long roleId);
}

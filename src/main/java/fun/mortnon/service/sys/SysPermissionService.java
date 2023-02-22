package fun.mortnon.service.sys;

import fun.mortnon.service.sys.vo.SysPermissionDTO;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import reactor.core.publisher.Mono;

/**
 * @author dev2007
 * @date 2023/2/20
 */
public interface SysPermissionService {
    Mono<Page<SysPermissionDTO>> queryPermission(Pageable pageable);
}

package fun.mortnon.service.sys;

import fun.mortnon.service.sys.vo.SysPermissionDTO;
import fun.mortnon.web.controller.role.command.CreatePermissionCommand;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author dev2007
 * @date 2023/2/20
 */
public interface SysPermissionService {
    /**
     * 查询权限
     *
     * @param pageable
     * @return
     */
    Mono<Page<SysPermissionDTO>> queryPermission(Pageable pageable);

    /**
     * 创建权限
     *
     * @param createPermissionCommand
     * @return
     */
    Mono<SysPermissionDTO> createPermission(CreatePermissionCommand createPermissionCommand);

    /**
     * 删除权限
     *
     * @param id
     * @return
     */
    Mono<Boolean> deletePermission(Long id);

    /**
     * 查询指定用户的权限信息
     *
     * @param userId
     * @return
     */
    Mono<List<SysPermissionDTO>> queryUserPermission(Long userId);
}

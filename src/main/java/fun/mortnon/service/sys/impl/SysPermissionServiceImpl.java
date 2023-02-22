package fun.mortnon.service.sys.impl;

import fun.mortnon.dal.sys.repository.PermissionRepository;
import fun.mortnon.service.sys.SysPermissionService;
import fun.mortnon.service.sys.vo.SysPermissionDTO;
import fun.mortnon.service.sys.vo.SysUserDTO;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dev2007
 * @date 2023/2/20
 */
@Singleton
@Slf4j
public class SysPermissionServiceImpl implements SysPermissionService {
    @Inject
    private PermissionRepository permissionRepository;

    @Override
    public Mono<Page<SysPermissionDTO>> queryPermission(Pageable pageable) {
        return permissionRepository.findAll(pageable).map(k -> {
            List<SysPermissionDTO> collect = k.getContent().stream().map(SysPermissionDTO::convert).collect(Collectors.toList());
            return Page.of(collect, k.getPageable(), k.getTotalSize());
        });
    }
}

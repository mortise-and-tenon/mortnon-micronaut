package fun.mortnon.dal.sys.repository;

import fun.mortnon.dal.sys.entity.SysPermission;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorPageableRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author dev2007
 * @date 2023/2/9
 */
@R2dbcRepository(dialect = Dialect.MYSQL)
public interface PermissionRepository extends ReactorPageableRepository<SysPermission, Long> {
    /**
     * 按 id 查询所有权限
     *
     * @param ids
     * @return
     */
    Flux<SysPermission> findByIdIn(List<Long> ids);

    /**
     * 名字或标识符重复
     *
     * @param name
     * @param identifier
     * @return
     */
    Mono<Boolean> existsByNameEqualsOrIdentifierEquals(String name, String identifier);
}

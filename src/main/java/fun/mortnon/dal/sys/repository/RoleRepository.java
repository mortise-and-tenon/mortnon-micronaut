package fun.mortnon.dal.sys.repository;

import fun.mortnon.dal.sys.entity.SysApiPermission;
import fun.mortnon.dal.sys.entity.SysRole;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import io.micronaut.data.repository.reactive.ReactorPageableRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author dev2007
 * @date 2023/2/9
 */
@R2dbcRepository(dialect = Dialect.MYSQL)
public interface RoleRepository extends ReactorPageableRepository<SysRole, Long> {
    /**
     * 按角色标识符查询角色
     *
     * @param identifier
     * @return
     */
    Flux<SysRole> findByIdentifier(String identifier);

    /**
     * 是否存在同名或同标识符的角色
     *
     * @param name
     * @param identifier
     * @return
     */
    Mono<Boolean> existsByNameOrIdentifier(String name, String identifier);

    /**
     * 是否存在非当前数据的重名
     *
     * @param id
     * @param name
     * @return
     */
    Mono<Boolean> existsByIdNotEqualsAndNameEquals(Long id, String name);
}

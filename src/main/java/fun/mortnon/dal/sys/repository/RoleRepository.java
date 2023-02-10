package fun.mortnon.dal.sys.repository;

import fun.mortnon.dal.sys.entity.SysApiPermission;
import fun.mortnon.dal.sys.entity.SysRole;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import reactor.core.publisher.Flux;

/**
 * @author dev2007
 * @date 2023/2/9
 */
@R2dbcRepository(dialect = Dialect.MYSQL)
public interface RoleRepository extends ReactorCrudRepository<SysRole, Long> {
    Flux<SysRole> findByIdentifier(String roleName);
}

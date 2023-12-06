package fun.mortnon.dal.sys.repository;

import fun.mortnon.dal.sys.entity.SysMenu;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorPageableRepository;
import reactor.core.publisher.Mono;

/**
 * @author dev2007
 * @date 2023/12/5
 */
@R2dbcRepository(dialect = Dialect.MYSQL)
public interface MenuRepository extends ReactorPageableRepository<SysMenu, Long> {
}

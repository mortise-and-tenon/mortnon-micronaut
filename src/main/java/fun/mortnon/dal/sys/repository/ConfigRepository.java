package fun.mortnon.dal.sys.repository;

import fun.mortnon.dal.sys.entity.SysConfig;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorPageableRepository;

/**
 * @author dev2007
 * @date 2024/3/15
 */
@R2dbcRepository(dialect = Dialect.MYSQL)
public interface ConfigRepository extends ReactorPageableRepository<SysConfig, Long> {
}

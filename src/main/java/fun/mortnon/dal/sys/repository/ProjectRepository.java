package fun.mortnon.dal.sys.repository;

import fun.mortnon.dal.sys.entity.SysProject;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorPageableRepository;

/**
 * @author dev2007
 * @date 2023/2/11
 */
@R2dbcRepository(dialect = Dialect.MYSQL)
public interface ProjectRepository extends ReactorPageableRepository<SysProject, Long> {
}

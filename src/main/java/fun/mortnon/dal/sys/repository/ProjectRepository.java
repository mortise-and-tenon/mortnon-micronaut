package fun.mortnon.dal.sys.repository;

import fun.mortnon.dal.sys.entity.SysProject;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorPageableRepository;
import reactor.core.publisher.Mono;

/**
 * @author dev2007
 * @date 2023/2/11
 */
@R2dbcRepository(dialect = Dialect.MYSQL)
public interface ProjectRepository extends ReactorPageableRepository<SysProject, Long> {
    /**
     * 是否存在同名
     *
     * @param name
     * @return
     */
    Mono<Boolean> existsByName(String name);

    /**
     * 是否是其他组织同名
     *
     * @param name
     * @param id
     * @return
     */
    Mono<Boolean> existsByNameEqualsAndIdNotEquals(String name, Long id);
}

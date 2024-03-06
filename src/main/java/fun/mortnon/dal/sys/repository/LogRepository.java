package fun.mortnon.dal.sys.repository;

import fun.mortnon.dal.sys.entity.SysLog;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.jpa.criteria.PredicateSpecification;
import io.micronaut.data.repository.reactive.ReactorPageableRepository;
import reactor.core.publisher.Mono;

/**
 * @author dev2007
 * @date 2023/3/7
 */
@R2dbcRepository(dialect = Dialect.MYSQL)
public interface LogRepository extends ReactorPageableRepository<SysLog, Long> {

    /**
     * 按条件分页查询
     *
     * @param spec
     * @param pageable
     * @return
     */
    Mono<Page<SysLog>> findAll(PredicateSpecification<SysLog> spec, Pageable pageable);
}

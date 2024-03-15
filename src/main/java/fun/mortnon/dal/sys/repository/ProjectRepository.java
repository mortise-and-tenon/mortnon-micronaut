package fun.mortnon.dal.sys.repository;

import fun.mortnon.dal.sys.entity.SysLog;
import fun.mortnon.dal.sys.entity.SysProject;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.jpa.criteria.PredicateSpecification;
import io.micronaut.data.repository.reactive.ReactorPageableRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author dev2007
 * @date 2023/2/11
 */
@R2dbcRepository(dialect = Dialect.MYSQL)
public interface ProjectRepository extends ReactorPageableRepository<SysProject, Long> {

    Flux<SysProject> findAll(PredicateSpecification<SysProject> where);

    Mono<Page<SysProject>> findAll(PredicateSpecification<SysProject> where, Pageable pageable);

    /**
     * 按标识值查找
     * @param identifier
     * @return
     */
    Mono<SysProject> findByIdentifier(String identifier);

    /**
     * 是否存在同名
     *
     * @param name
     * @return
     */
    Mono<Boolean> existsByName(String name);

    /**
     * 是否存在标识值相同
     *
     * @param identifier
     * @return
     */
    Mono<Boolean> existsByIdentifier(String identifier);

    /**
     * 是否是其他组织同名
     *
     * @param name
     * @param id
     * @return
     */
    Mono<Boolean> existsByNameEqualsAndIdNotEquals(String name, Long id);

    Mono<Long> deleteByIdIn(List<Long> idList);
}

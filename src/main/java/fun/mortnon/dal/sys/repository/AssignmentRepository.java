package fun.mortnon.dal.sys.repository;

import fun.mortnon.dal.sys.entity.SysAssignment;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorPageableRepository;
import reactor.core.publisher.Mono;

/**
 * @author dev2007
 * @date 2023/2/9
 */
@R2dbcRepository(dialect = Dialect.MYSQL)
public interface AssignmentRepository extends ReactorPageableRepository<SysAssignment, Long> {
    /**
     * 按用户 id 查找关联关系
     *
     * @param userId
     * @return
     */
    Mono<SysAssignment> findByUserId(Long userId);
}

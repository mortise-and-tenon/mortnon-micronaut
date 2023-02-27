package fun.mortnon.dal.sys.repository;

import fun.mortnon.dal.sys.entity.SysAssignment;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorPageableRepository;
import reactor.core.publisher.Flux;
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
    Flux<SysAssignment> findByUserId(Long userId);

    /**
     * 按角色 id 查找关联关系
     *
     * @param roleId
     * @return
     */
    Flux<SysAssignment> findByRoleId(Long roleId);

    /**
     * 按用户 id 判断是否存在关联关系
     *
     * @param userId
     * @return
     */
    Mono<Boolean> existsByUserId(Long userId);

    /**
     * 按角色 id 查找是否存在关联关系
     *
     * @param roleId
     * @return
     */
    Mono<Boolean> existsByRoleId(Long roleId);

    /**
     * 按组织 id 查找是否存在关联关系
     *
     * @param projectId
     * @return
     */
    Mono<Boolean> existsByProjectId(Long projectId);

    /**
     * 判断关联是否存在
     *
     * @param userId
     * @param projectId
     * @param roleId
     * @return
     */
    Mono<Boolean> existsByUserIdEqualsAndProjectIdEqualsAndRoleIdEquals(Long userId, Long projectId, Long roleId);

    /**
     * 删除指定的关联
     *
     * @param userId
     * @param projectId
     * @param roleId
     * @return
     */
    Mono<Long> deleteByUserIdEqualsAndProjectIdEqualsAndRoleIdEquals(Long userId, Long projectId, Long roleId);
}

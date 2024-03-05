package fun.mortnon.dal.sys.repository;

import fun.mortnon.dal.sys.entity.SysAssignment;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorPageableRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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

    Flux<SysAssignment> findByUserIdIn(List<Long> userIdList);

    /**
     * 按角色 id 查找关联关系
     *
     * @param roleId
     * @return
     */
    Flux<SysAssignment> findByRoleId(Long roleId);

    /**
     * 查找分配了组织的关联
     *
     * @return
     */
    Flux<SysAssignment> findByProjectIdIsNotNull();

    /**
     * 查找分配了角色的关联
     *
     * @param
     * @return
     */
    Flux<SysAssignment> findByRoleIdIsNotNull();

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
     * 按用户和组织查找是否存在关联关系
     *
     * @param userId
     * @param projectId
     * @return
     */
    Mono<Boolean> existsByUserIdEqualsAndProjectIdEquals(Long userId, Long projectId);

    /**
     * 按用户和角色查询是否存在关联关系
     *
     * @param userId
     * @param roleId
     * @return
     */
    Mono<Boolean> existsByUserIdEqualsAndRoleIdEquals(Long userId, Long roleId);

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
     * 按用户和组织查询指定的关联
     *
     * @param userId
     * @param projectId
     * @return
     */
    Mono<SysAssignment> findByUserIdEqualsAndProjectIdEquals(Long userId, Long projectId);

    /**
     * 按用户和角色查询指定关联
     *
     * @param userId
     * @param roleId
     * @return
     */
    Mono<SysAssignment> findByUserIdEqualsAndRoleIdEquals(Long userId, Long roleId);

    /**
     * 删除指定的关联
     *
     * @param userId
     * @param projectId
     * @param roleId
     * @return
     */
    Mono<Long> deleteByUserIdEqualsAndProjectIdEqualsAndRoleIdEquals(Long userId, Long projectId, Long roleId);

    /**
     * 按用户id删除关联
     *
     * @param userId
     * @return
     */
    Mono<Long> deleteByUserId(Long userId);

    /**
     * 按用户id批量删除关联
     *
     * @param userIdList
     * @return
     */
    Mono<Long> deleteByUserIdInList(List<Long> userIdList);
}

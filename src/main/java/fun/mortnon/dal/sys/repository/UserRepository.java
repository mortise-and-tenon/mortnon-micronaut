package fun.mortnon.dal.sys.repository;

import fun.mortnon.dal.sys.entity.SysUser;
import io.micronaut.core.annotation.Nullable;
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
 * 系统用户仓库
 *
 * @author dev2007
 * @date 2023/2/3
 */
@R2dbcRepository(dialect = Dialect.MYSQL)
public interface UserRepository extends ReactorPageableRepository<SysUser, Long> {
    /**
     * 保存用户
     *
     * @param sysUser The entity to save. Must not be {@literal null}.
     * @return
     */
    Mono<SysUser> save(@Nullable SysUser sysUser);

    /**
     * 按用户名查找用户
     *
     * @param userName
     * @return
     */
    Mono<SysUser> findByUserName(String userName);

    /**
     * 按用户 id 或用户名查找用户
     *
     * @param id
     * @param userName
     * @return
     */
    Mono<SysUser> findByIdOrUserName(Long id, String userName);

    /**
     * 按用户名删除用户
     *
     * @param userName
     * @return
     */
    Mono<Long> deleteByUserName(String userName);

    /**
     * 判断用户名是否存在
     *
     * @param userName
     * @return
     */
    Mono<Boolean> existsByUserName(String userName);

    /**
     * 按用户 id 或用户名判断用户是否存在
     *
     * @param id
     * @param userName
     * @return
     */
    Mono<Boolean> existsByIdEqualsOrUserNameEquals(Long id, String userName);

    /**
     * 查找用户名是否存在
     *
     * @param userName
     * @return
     */
    Mono<Boolean> existsByUserNameInList(List<String> userName);

    /**
     * 查找列表中用户名的数据
     *
     * @param userName
     * @return
     */
    Flux<SysUser> findByUserNameInList(List<String> userName);

    /**
     * 带条件的分页查询
     *
     * @param where
     * @param pageable
     * @return
     */
    Mono<Page<SysUser>> findAll(PredicateSpecification<SysUser> where, Pageable pageable);

    /**
     * 按用户id批量删除用户
     *
     * @param userIdList
     * @return
     */
    Mono<Long> deleteByIdInList(List<Long> userIdList);

    /**
     * 按用户id批量查询用户
     *
     * @param userIdList
     * @return
     */
    Flux<SysUser> findByIdInList(List<Long> userIdList);
}

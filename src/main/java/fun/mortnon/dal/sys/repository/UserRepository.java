package fun.mortnon.dal.sys.repository;

import fun.mortnon.dal.sys.entity.SysUser;
import fun.mortnon.service.sys.vo.SysUserDTO;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Slice;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorPageableRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

}

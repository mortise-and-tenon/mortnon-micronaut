package fun.mortnon.dal.sys.repository;

import fun.mortnon.dal.sys.entity.SysApi;
import fun.mortnon.dal.sys.entity.SysAssignment;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorPageableRepository;
import io.micronaut.http.HttpMethod;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author dev2007
 * @date 2023/2/9
 */
@R2dbcRepository(dialect = Dialect.MYSQL)
public interface ApiRepository extends ReactorPageableRepository<SysApi, Long> {
    /**
     * 按权限查找对应的API
     *
     * @param identifier
     * @return
     */
    Flux<SysApi> findByIdentifier(String identifier);

    /**
     * 按权限和 API 方法查找
     *
     * @param identifier
     * @param method
     * @return
     */
    Flux<SysApi> findByIdentifierAndMethod(String identifier, HttpMethod method);

    /**
     * 是否存在对应的 API
     *
     * @param identifier
     * @param method
     * @return
     */
    Mono<Boolean> existsByIdentifierAndMethod(String identifier, HttpMethod method);
}

package fun.mortnon.dal.sys.repository;

import fun.mortnon.dal.sys.entity.SysTemplate;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorPageableRepository;
import reactor.core.publisher.Mono;

/**
 * 消息模板仓库
 *
 * @author dev2007
 * @date 2024/3/21
 */
@R2dbcRepository(dialect = Dialect.MYSQL)
public interface TemplateRepository extends ReactorPageableRepository<SysTemplate, Long> {
    /**
     * 按模板名字查找模板
     *
     * @param name
     * @return
     */
    Mono<SysTemplate> findByName(String name);

    /**
     * 按模板名字判断模板是否存在
     *
     * @param name
     * @return
     */
    Mono<Boolean> existsByName(String name);
}

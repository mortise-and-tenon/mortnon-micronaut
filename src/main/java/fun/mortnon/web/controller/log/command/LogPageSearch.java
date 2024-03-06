package fun.mortnon.web.controller.log.command;

import fun.mortnon.framework.vo.PageableQuery;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

import java.time.Instant;

/**
 * 日志查询数据
 * {key}={value}
 *
 * @author dev2007
 * @date 2024/2/27
 */
@Data
@Serdeable(naming = SnakeCaseStrategy.class)
@Introspected
public class LogPageSearch extends PageableQuery {

    /**
     * ip
     */
    private String ip;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户所在组织名
     */
    private String projectName;

    /**
     * 操作类型
     */
    private String action;

    /**
     * 操作结果
     */
    private String result;

    /**
     * 操作级别
     */
    private String level;

    /**
     * 用于查询日志时间范围：开始时间
     */
    private String beginTime;

    /**
     * 用于查询日志时间范围：结束时间
     */
    private String endTime;
}

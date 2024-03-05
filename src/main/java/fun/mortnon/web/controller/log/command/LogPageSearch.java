package fun.mortnon.web.controller.log.command;

import fun.mortnon.framework.vo.PageableQuery;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

import java.time.Instant;

/**
 * 模糊查询参数
 * {key}={value}
 *
 * @author dev2007
 * @date 2024/2/27
 */
@Data
@Serdeable(naming = SnakeCaseStrategy.class)
@Introspected
public class LogPageSearch extends PageableQuery {
    private String ip;
    private String userName;
    private String projectName;
    private String action;
    private String result;
    private String level;

    /**
     * 用于查询日志时间：开始时间
     */
    private String beginTime;

    /**
     * 用于查询日志时间：结束时间
     */
    private String endTime;
}

package fun.mortnon.web.controller.log.command;

import fun.mortnon.framework.vo.PageableQuery;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

import java.time.Instant;

/**
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
    private Instant beginTime;
    private Instant endTime;
}

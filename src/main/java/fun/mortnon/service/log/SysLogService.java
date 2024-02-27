package fun.mortnon.service.log;

import fun.mortnon.dal.sys.entity.SysLog;
import fun.mortnon.service.log.vo.SysLogDTO;
import fun.mortnon.web.controller.log.command.LogPageSearch;
import fun.mortnon.web.vo.login.MortnonDefaultPageable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import reactor.core.publisher.Mono;

/**
 * @author dev2007
 * @date 2023/3/7
 */
public interface SysLogService {

    /**
     * 创建操作日志
     *
     * @param sysLog
     */
    Mono<SysLog> createLog(SysLog sysLog);

    /**
     * 查询操作日志
     *
     * @param pageSearch
     * @param lang
     * @return
     */
    Mono<Page<SysLogDTO>> queryLogs(LogPageSearch pageSearch, String lang);
}

package fun.mortnon.service.log;

import fun.mortnon.dal.sys.entity.SysLog;
import fun.mortnon.service.log.vo.SysLogDTO;
import fun.mortnon.web.controller.log.command.LogPageSearch;
import fun.mortnon.web.vo.login.MortnonDefaultPageable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.server.types.files.StreamedFile;
import io.micronaut.http.server.types.files.SystemFile;
import reactor.core.publisher.Mono;

/**
 * 操作日志服务
 *
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

    /**
     * 导出日志文件
     *
     * @param pageSearch
     * @param lang
     * @return
     */
    Mono<SystemFile> exportFile(LogPageSearch pageSearch, String lang);

    /**
     * 生成日志
     *
     * @param request
     * @param response
     */
    void buildLog(HttpRequest<Object> request, HttpResponse response);
}

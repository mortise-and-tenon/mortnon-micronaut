package fun.mortnon.web.controller.log;

import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.framework.vo.PageableData;
import fun.mortnon.framework.vo.PageableQuery;
import fun.mortnon.service.log.SysLogService;
import fun.mortnon.service.log.vo.SysLogDTO;
import fun.mortnon.service.sys.vo.SysUserDTO;
import fun.mortnon.web.controller.log.command.LogPageSearch;
import fun.mortnon.web.vo.login.MortnonDefaultPageable;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.annotation.*;
import io.micronaut.http.server.types.files.SystemFile;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

/**
 * 操作日志
 *
 * @author dev2007
 * @date 2023/3/9
 */
@Controller("/logs")
public class LogController {
    /**
     * 日志服务
     */
    @Inject
    private SysLogService sysLogService;

    /**
     * 查询日志
     *
     * @param pageSearch 查询参数、分页、排序
     * @param lang       语言
     * @return
     */
    @Get("{?pageSearch*}")
    public Mono<MortnonResult<PageableData<List<SysLogDTO>>>> queryUser(LogPageSearch pageSearch,
                                                                        @QueryValue(defaultValue = "zh") String lang) {
        return sysLogService.queryLogs(pageSearch, lang).map(MortnonResult::successPageData);
    }

    /**
     * 导出日志
     *
     * @param pageSearch 查询参数、分页、排序
     * @param lang       语言
     * @return
     */
    @Get("/export{?pageSearch*}")
    public Mono<SystemFile> exportFile(LogPageSearch pageSearch, @QueryValue(defaultValue = "zh") String lang) {
        return sysLogService.exportFile(pageSearch, lang);
    }
}

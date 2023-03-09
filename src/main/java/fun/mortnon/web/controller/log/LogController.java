package fun.mortnon.web.controller.log;

import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.framework.vo.PageableData;
import fun.mortnon.service.log.SysLogService;
import fun.mortnon.service.log.vo.SysLogDTO;
import fun.mortnon.service.sys.vo.SysUserDTO;
import fun.mortnon.web.vo.login.MortnonDefaultPageable;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.annotation.*;
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
    @Inject
    private SysLogService sysLogService;

    @Get
    public Mono<MortnonResult<PageableData<List<SysLogDTO>>>> queryUser(@Valid Pageable pageable, @QueryValue String lang) {
        return sysLogService.queryLogs(pageable, lang).map(MortnonResult::successPageData);
    }
}

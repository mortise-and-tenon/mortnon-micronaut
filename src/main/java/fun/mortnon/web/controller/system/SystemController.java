package fun.mortnon.web.controller.system;

import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.service.sys.ConfigService;
import fun.mortnon.service.sys.vo.SysAllConfigDTO;
import fun.mortnon.service.sys.vo.SysConfigDTO;
import fun.mortnon.web.controller.system.command.UpdateConfigCommand;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

/**
 * 系统相关
 *
 * @author dev2007
 * @date 2024/3/15
 */
@Controller("/system")
@Slf4j
public class SystemController {

    /**
     * 配置服务
     */
    @Inject
    private ConfigService configService;

    /**
     * 查询登录用相关配置
     *
     * @return
     */
    @Secured(SecurityRule.IS_ANONYMOUS)
    @Get("/login/config")
    public Mono<MortnonResult<SysConfigDTO>> queryConfig() {
        return configService.queryLoginConfig()
                .map(MortnonResult::success);
    }

    /**
     * 查询系统配置
     *
     * @return
     */
    @Get("/config")
    public Mono<MortnonResult<SysAllConfigDTO>> queryAllConfig() {
        return configService.queryConfig()
                .map(SysAllConfigDTO::convert)
                .map(MortnonResult::success);
    }

    /**
     * 更新配置
     *
     * @param update
     * @return
     */
    @Put("/config")
    public Mono<MutableHttpResponse<MortnonResult>> updateConfig(@Body @NotNull UpdateConfigCommand update) {
        return configService.updateConfig(update)
                .map(SysConfigDTO::convert)
                .map(MortnonResult::success)
                .map(HttpResponse::ok);
    }
}

package fun.mortnon.service.sys;

import fun.mortnon.dal.sys.entity.SysConfig;
import fun.mortnon.service.sys.vo.SysAllConfigDTO;
import fun.mortnon.service.sys.vo.SysConfigDTO;
import fun.mortnon.web.controller.system.command.UpdateConfigCommand;
import reactor.core.publisher.Mono;

/**
 * 配置服务
 *
 * @author dev2007
 * @date 2024/3/15
 */
public interface ConfigService {

    /**
     * 查询系统配置
     *
     * @return
     */
    Mono<SysConfig> queryConfig();

    /**
     * 获取用于登录相关的配置
     *
     * @return
     */
    Mono<SysConfigDTO> queryLoginConfig();

    /**
     * 更新系统配置
     *
     * @param update
     * @return
     */
    Mono<SysConfig> updateConfig(UpdateConfigCommand update);
}

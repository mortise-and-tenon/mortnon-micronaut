package fun.mortnon.service.sys.vo;

import fun.mortnon.dal.sys.entity.SysMenu;
import fun.mortnon.framework.utils.MortnonBeanUtils;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dev2007
 * @date 2023/12/6
 */
@Introspected
@Serdeable(naming = SnakeCaseStrategy.class)
@Data
public class SysMenuTreeDTO extends SysMenuDTO {
    /**
     * 子菜单
     */
    private List<SysMenuTreeDTO> children;

    public static SysMenuTreeDTO convert(SysMenu sysMenu) {
        SysMenuTreeDTO sysMenuTreeDTO = new SysMenuTreeDTO();
        MortnonBeanUtils.copy(sysMenu,sysMenuTreeDTO);
        sysMenuTreeDTO.setChildren(new ArrayList<>());
        return sysMenuTreeDTO;
    }
}

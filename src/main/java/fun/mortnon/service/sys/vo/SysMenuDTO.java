package fun.mortnon.service.sys.vo;

import fun.mortnon.dal.sys.entity.SysMenu;
import fun.mortnon.dal.sys.entity.config.MenuType;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

import java.time.Instant;

/**
 * @author dev2007
 * @date 2024/3/6
 */
@Introspected
@Serdeable(naming = SnakeCaseStrategy.class)
@Data
public class SysMenuDTO {
    /**
     * 菜单id
     */
    private Long id;

    /**
     * 父菜单 id
     */
    private Long parentId;

    /**
     * 菜单名字
     */
    private String name;

    /**
     * 菜单 url
     */
    private String url;

    /**
     * 菜单类型
     */
    private MenuType type;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 菜单权限
     */
    private String permission;

    /**
     * 菜单顺序
     */
    private int order;

    /**
     * 菜单状态
     */
    private boolean status;

    /**
     * 创建时间
     */
    private Instant gmtCreate;

    public static SysMenuDTO convert(SysMenu sysMenu) {
        SysMenuDTO sysMenuDTO = new SysMenuDTO();

        sysMenuDTO.setId(sysMenu.getId());
        sysMenuDTO.setParentId(sysMenu.getParentId());
        sysMenuDTO.setName(sysMenu.getName());
        sysMenuDTO.setUrl(sysMenu.getUrl());
        sysMenuDTO.setIcon(sysMenu.getIcon());
        sysMenuDTO.setPermission(sysMenu.getPermission());
        sysMenuDTO.setOrder(sysMenu.getOrder());
        sysMenuDTO.setStatus(sysMenu.isStatus());
        sysMenuDTO.setGmtCreate(sysMenu.getGmtCreate());

        return sysMenuDTO;
    }
}

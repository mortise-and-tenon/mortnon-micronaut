package fun.mortnon.service.sys.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import fun.mortnon.dal.sys.entity.SysMenu;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author dev2007
 * @date 2023/12/6
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
     * 子菜单
     */
    private List<SysMenuDTO> children;

    public static SysMenuDTO convert(SysMenu sysMenu) {
        SysMenuDTO sysMenuDTO = new SysMenuDTO();
        sysMenuDTO.setId(sysMenu.getId());
        sysMenuDTO.setName(sysMenu.getName());
        sysMenuDTO.setUrl(sysMenu.getUrl());
        sysMenuDTO.setIcon(sysMenu.getIcon());
        sysMenuDTO.setOrder(sysMenu.getOrder());

        String permission = sysMenu.getPermission();
        sysMenuDTO.setPermission(permission);

        sysMenuDTO.setParentId(sysMenu.getParentId());
        return sysMenuDTO;
    }
}

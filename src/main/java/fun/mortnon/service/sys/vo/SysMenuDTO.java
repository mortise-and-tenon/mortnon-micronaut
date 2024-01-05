package fun.mortnon.service.sys.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import fun.mortnon.dal.sys.entity.SysMenu;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author dev2007
 * @date 2023/12/6
 */
@Introspected
@Serdeable
@Data
public class SysMenuDTO {
    /**
     * 菜单id
     */
    private Long id;

    /**
     * 父菜单 id
     */
    @JsonProperty(value = "parent_id")
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
    private List<String> permission;

    /**
     * 菜单顺序
     */
    private int order;

    /**
     * 子菜单
     */
    @JsonProperty(value = "children_menu")
    private List<SysMenuDTO> childrenMenu;

    public static SysMenuDTO convert(SysMenu sysMenu) {
        SysMenuDTO sysMenuDTO = new SysMenuDTO();
        sysMenuDTO.setId(sysMenu.getId());
        sysMenuDTO.setName(sysMenu.getName());
        sysMenuDTO.setUrl(sysMenu.getUrl());
        sysMenuDTO.setIcon(sysMenu.getIcon());

        String permission = sysMenu.getPermission();
        sysMenuDTO.setPermission(Arrays.asList(permission.split(",")));

        sysMenuDTO.setParentId(sysMenu.getParentId());
        return sysMenuDTO;
    }
}

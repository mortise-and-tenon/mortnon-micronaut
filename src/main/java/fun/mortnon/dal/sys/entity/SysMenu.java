package fun.mortnon.dal.sys.entity;

import fun.mortnon.dal.base.entity.BaseEntity;
import fun.mortnon.dal.sys.entity.config.MenuType;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

/**
 * 菜单
 *
 * @author dev2007
 * @date 2023/12/5
 */
@Serdeable
@MappedEntity
@Data
public class SysMenu extends BaseEntity {
    private String name;
    private Long parentId;
    private int order;
    private String url;
    private MenuType type;
    private String icon;
    private String permission;
    private boolean status;
}

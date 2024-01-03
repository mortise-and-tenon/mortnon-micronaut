package fun.mortnon.service.sys.vo;

import fun.mortnon.dal.sys.entity.SysProject;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dev2007
 * @date 2024/1/3
 */
@Introspected
@Serdeable
@Data
public class SysProjectTreeDTO {
    /**
     * 组织 id
     */
    private Long id;

    /**
     * 组织名字
     */
    private String name;

    /**
     * 组织描述
     */
    private String description;

    /**
     * 子组织
     */
    private List<SysProjectTreeDTO> children;

    public static SysProjectTreeDTO convert(SysProject sysProject) {
        SysProjectTreeDTO sysProjectTreeDTO = new SysProjectTreeDTO();
        sysProjectTreeDTO.setId(sysProject.getId());
        sysProjectTreeDTO.setName(sysProject.getName());
        sysProjectTreeDTO.setDescription(sysProject.getDescription());
        return sysProjectTreeDTO;
    }
}

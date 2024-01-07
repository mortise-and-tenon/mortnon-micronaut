package fun.mortnon.service.sys.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import fun.mortnon.dal.sys.entity.SysProject;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

/**
 * @author dev2007
 * @date 2023/2/24
 */
@Introspected
@Serdeable(naming = SnakeCaseStrategy.class)
@Data
public class SysProjectDTO {

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
     * 父组织 id
     */
    private Long parentId;

    public static SysProjectDTO convert(SysProject sysProject) {
        SysProjectDTO sysProjectDTO = new SysProjectDTO();
        sysProjectDTO.setId(sysProject.getId());
        sysProjectDTO.setName(sysProject.getName());
        sysProjectDTO.setDescription(sysProject.getDescription());
        sysProjectDTO.setParentId(sysProject.getParentId());
        return sysProjectDTO;
    }
}

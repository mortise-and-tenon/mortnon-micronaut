package fun.mortnon.service.sys.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import fun.mortnon.dal.sys.entity.SysProject;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.Instant;

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

    /**
     * 组织排序
     */
    private int order;

    /**
     * 组织状态
     */
    private boolean status;

    /**
     * 创建时间
     */
    private Instant time;

    public static SysProjectDTO convert(SysProject sysProject) {
        SysProjectDTO sysProjectDTO = new SysProjectDTO();
        sysProjectDTO.setId(sysProject.getId());
        sysProjectDTO.setName(sysProject.getName());
        sysProjectDTO.setDescription(sysProject.getDescription() == null ? "" : sysProject.getDescription());
        sysProjectDTO.setParentId(sysProject.getParentId());
        sysProjectDTO.setOrder(sysProject.getOrder());
        sysProjectDTO.setStatus(sysProject.isStatus());
        sysProjectDTO.setTime(sysProject.getGmtCreate());
        return sysProjectDTO;
    }
}

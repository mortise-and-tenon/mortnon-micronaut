package fun.mortnon.service.sys.vo;

import fun.mortnon.dal.sys.entity.SysProject;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
     * 组织标识值
     */
    private String identifier;

    /**
     * 组织描述
     */
    private String description;

    /**
     * 子组织
     */
    private List<SysProjectTreeDTO> children;

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

    public static SysProjectTreeDTO convert(SysProject sysProject) {
        SysProjectTreeDTO sysProjectTreeDTO = new SysProjectTreeDTO();

        sysProjectTreeDTO.setId(sysProject.getId());
        sysProjectTreeDTO.setName(sysProject.getName());
        sysProjectTreeDTO.setIdentifier(sysProject.getIdentifier());
        sysProjectTreeDTO.setOrder(sysProject.getOrder());
        sysProjectTreeDTO.setStatus(sysProject.isStatus());
        sysProjectTreeDTO.setTime(sysProject.getGmtCreate());

        sysProjectTreeDTO.setDescription(Optional.ofNullable(sysProject.getDescription()).orElse(""));
        sysProjectTreeDTO.setChildren(new ArrayList<>());
        return sysProjectTreeDTO;
    }
}

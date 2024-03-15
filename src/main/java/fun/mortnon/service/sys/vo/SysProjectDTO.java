package fun.mortnon.service.sys.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import fun.mortnon.dal.sys.entity.SysProject;
import fun.mortnon.framework.utils.MortnonBeanUtils;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;

import javax.validation.constraints.NotEmpty;
import java.time.Instant;
import java.util.Optional;

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
     * 组织标识值
     */
    private String identifier;

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
        MortnonBeanUtils.copy(sysProject, sysProjectDTO);
        sysProjectDTO.setDescription(Optional.ofNullable(sysProject.getDescription()).orElse(""));
        return sysProjectDTO;
    }
}

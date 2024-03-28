package fun.mortnon.service.sys.vo;

import fun.mortnon.dal.sys.entity.SysUser;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dev2007
 * @date 2023/2/8
 */
@Introspected
@Serdeable(naming = SnakeCaseStrategy.class)
@Data
public class SysUserDTO {
    /**
     * 用户 id
     */
    private Long id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户手机号
     */
    private String phone;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 用户性别
     */
    private Integer sex;

    /**
     * 用户状态
     */
    private boolean status;

    /**
     * 用户创建时间
     */
    private Instant gmtCreate;

    /**
     * 关联的组织、角色
     */
    private List<ProjectRoleDTO> projectRoles;

    public static SysUserDTO convert(SysUser sysUser) {
        SysUserDTO sysUserDTO = new SysUserDTO();

        sysUserDTO.setId(sysUser.getId());
        sysUserDTO.setUserName(sysUser.getUserName());
        sysUserDTO.setNickName(sysUser.getNickName());
        sysUserDTO.setEmail(sysUser.getEmail());
        sysUserDTO.setPhone(sysUser.getPhone());
        sysUserDTO.setAvatar(sysUser.getAvatar());
        sysUserDTO.setSex(sysUser.getSex());
        sysUserDTO.setStatus(sysUser.isStatus());
        sysUserDTO.setGmtCreate(sysUser.getGmtCreate());
        sysUserDTO.setProjectRoles(new ArrayList<>());
        return sysUserDTO;
    }
}

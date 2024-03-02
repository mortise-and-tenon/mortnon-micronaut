package fun.mortnon.service.sys.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fun.mortnon.dal.sys.entity.SysUser;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

import java.awt.*;
import java.time.Instant;
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

    public SysUserDTO() {

    }

    private SysUserDTO(Long id, String userName, String nickName, String email, String phone, String avatar, int sex, boolean status, Instant gmtCreate) {
        setId(id);
        setUserName(userName);
        setNickName(nickName);
        setEmail(email);
        setPhone(phone);
        setAvatar(avatar);
        setSex(sex);
        setStatus(status);
        setGmtCreate(gmtCreate);
    }

    public static SysUserDTO convert(SysUser sysUser) {
        if (null == sysUser) {
            return new SysUserDTO();
        }

        return new SysUserDTO(sysUser.getId(), sysUser.getUserName(), sysUser.getNickName(), sysUser.getEmail(),
                sysUser.getPhone(), sysUser.getAvatar(), sysUser.getSex(), sysUser.isStatus(), sysUser.getGmtCreate());
    }
}

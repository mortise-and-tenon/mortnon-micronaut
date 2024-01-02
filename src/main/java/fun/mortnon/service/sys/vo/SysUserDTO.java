package fun.mortnon.service.sys.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fun.mortnon.dal.sys.entity.SysUser;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

import java.awt.*;
import java.util.List;

/**
 * @author dev2007
 * @date 2023/2/8
 */
@Introspected
@Serdeable
@Data
public class SysUserDTO {
    private Long id;
    private String userName;
    private String nickName;
    private String email;
    private String phone;
    private String avatar;
    private Integer sex;

    @JsonProperty(value = "project_roles")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private List<ProjectRoleDTO> projectRoles;

    public SysUserDTO() {

    }

    public SysUserDTO(Long id, String userName, String nickName, String email, String phone, String avatar, int sex) {
        setId(id);
        setUserName(userName);
        setNickName(nickName);
        setEmail(email);
        setPhone(phone);
        setAvatar(avatar);
        setSex(sex);
    }

    public static SysUserDTO convert(SysUser sysUser) {
        if (null == sysUser) {
            return new SysUserDTO();
        }

        return new SysUserDTO(sysUser.getId(), sysUser.getUserName(), sysUser.getNickName(), sysUser.getEmail(),
                sysUser.getPhone(), sysUser.getAvatar(), sysUser.getSex());
    }
}

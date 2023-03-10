package fun.mortnon.service.sys.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import fun.mortnon.dal.sys.entity.SysUser;
import fun.mortnon.framework.enums.Sex;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author dev2007
 * @date 2023/2/8
 */
@Data
public class SysUserDTO {
    private Long id;
    private String userName;
    private String nickName;
    private String email;
    private String phone;
    private String head;
    private Integer sex;

    @JsonProperty(value = "project_roles")
    private List<ProjectRoleDTO> projectRoles;

    public SysUserDTO() {

    }

    public SysUserDTO(Long id, String userName, String nickName, String email, String phone, String head, int sex) {
        setId(id);
        setUserName(userName);
        setNickName(nickName);
        setEmail(email);
        setPhone(phone);
        setHead(head);
        setSex(sex);
    }

    public static SysUserDTO convert(SysUser sysUser) {
        if (null == sysUser) {
            return new SysUserDTO();
        }

        return new SysUserDTO(sysUser.getId(), sysUser.getUserName(), sysUser.getNickName(), sysUser.getEmail(),
                sysUser.getPhone(), sysUser.getHead(), sysUser.getSex());
    }
}

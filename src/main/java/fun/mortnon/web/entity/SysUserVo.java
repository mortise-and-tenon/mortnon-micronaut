package fun.mortnon.web.entity;

import lombok.Data;

/**
 * @author dev2007
 * @date 2023/2/6
 */
@Data
public class SysUserVo {
    private String userName;

    private String nickName;

    private String password;

    private String salt;

    private String email;

    private String phone;

    private String head;

    private Integer sex;

    private Long tenantId;
}

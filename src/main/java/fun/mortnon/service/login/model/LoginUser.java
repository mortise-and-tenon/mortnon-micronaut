package fun.mortnon.service.login.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fun.mortnon.service.login.enums.LoginType;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * @author dongfangzan
 * @date 27.4.21 3:31 下午
 */
@Data
@Accessors(chain = true)
@Serdeable
public class LoginUser {

    /** 登录用户id  */
    private String id;

    /** 用户名唯一键 */
    private String username;

    /** 登录类型 */
    @JsonIgnore
    private LoginType loginType;

    /** 盐 */
    @JsonIgnore
    private String salt;

    /** 权限码 */
    private Set<String> permissionCodes;

    /** 角色码 */
    private Set<String> roles;
}

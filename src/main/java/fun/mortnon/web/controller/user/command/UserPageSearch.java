package fun.mortnon.web.controller.user.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import fun.mortnon.framework.vo.PageableQuery;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * @author dev2007
 * @date 2024/3/2
 */
@Data
@Serdeable(naming = SnakeCaseStrategy.class)
@Introspected
public class UserPageSearch extends PageableQuery {
    /**
     * 角色id
     */
    @NotNull
    @Positive
    @JsonProperty(value = "role_id")
    private Long roleId;

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
     * 性别
     */
    private Integer sex;

    /**
     * 用户手机号
     */
    private String phone;
}

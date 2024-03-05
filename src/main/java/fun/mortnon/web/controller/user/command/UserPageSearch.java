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
     * 是否查询未分配的
     */
    private boolean unassignment;
    /**
     * 角色id
     */
    @Positive
    @JsonProperty(value = "role_id")
    private Long roleId;


    /**
     * 组织id
     */
    @Positive
    @JsonProperty(value = "project_id")
    private Long projectId;

    /**
     * 用户名
     */
    @JsonProperty(value = "user_name")
    private String userName;

    /**
     * 用户昵称
     */
    @JsonProperty(value = "nick_name")
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

    /**
     * 用户状态
     */
    private Boolean status;
}

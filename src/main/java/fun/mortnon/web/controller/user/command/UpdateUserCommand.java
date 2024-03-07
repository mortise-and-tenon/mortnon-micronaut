package fun.mortnon.web.controller.user.command;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * 修改用户数据
 *
 * @author dev2007
 * @date 2023/2/17
 */
@Introspected
@Serdeable(naming = SnakeCaseStrategy.class)
@Data
public class UpdateUserCommand {
    /**
     * 用户 id
     */
    @NotNull
    @Positive
    private Long id;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 邮箱
     */
    @Email
    private String email;

    /**
     * 电话
     */
    private String phone;

    /**
     * 性别
     */
    @PositiveOrZero
    @Min(0)
    @Max(1)
    private Integer sex;

    /**
     * 状态
     */
    private Boolean status;
}

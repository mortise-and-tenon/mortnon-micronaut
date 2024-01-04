package fun.mortnon.web.controller.user.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * @author dev2007
 * @date 2023/2/17
 */
@Introspected
@Serdeable
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
     * 头像
     */
    private String head;

    /**
     * 性别
     */
    @PositiveOrZero
    @Min(0)
    @Max(1)
    private Integer sex;
}

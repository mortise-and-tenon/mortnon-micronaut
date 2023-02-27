package fun.mortnon.web.controller.user.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author dev2007
 * @date 2023/2/17
 */
@Data
public class UpdateUserCommand {
    /**
     * 用户 id
     */
    private Long id;

    /**
     * 昵称
     */
    @JsonProperty("nick_name")
    private String nickName;

    /**
     * 邮箱
     */
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
    private Integer sex;
}

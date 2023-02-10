package fun.mortnon.dal.sys.entity;

import fun.mortnon.dal.base.entity.BaseEntity;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;


/**
 * 系统用户领域
 *
 * @author dongfangzan
 * @date 28.4.21 3:37 下午
 */
@Serdeable
@MappedEntity
@Data
public class SysUser extends BaseEntity {

    /**
     * 用户名
     */
    @NonNull
    private String userName;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 密码
     */
    private String password;

    /**
     * 盐
     */
    private String salt;

    /**
     * email
     */
    private String email;

    /**
     * 手机号码
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

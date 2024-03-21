package fun.mortnon.dal.sys.entity;

import fun.mortnon.dal.base.entity.BaseEntity;
import fun.mortnon.dal.sys.entity.config.CaptchaType;
import fun.mortnon.dal.sys.entity.config.DoubleFactorType;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

/**
 * 公开给登录页面使用的系统配置
 *
 * @author dev2007
 * @date 2024/3/15
 */
@MappedEntity
@Data
@Serdeable
public class SysConfig extends BaseEntity {
    /**
     * 是否开启验证码
     */
    private CaptchaType captcha;

    /**
     * 是否密码加密传输
     */
    private boolean passwordEncrypt;

    /**
     * 密码重试次数
     */
    private int tryCount;

    /**
     * 锁定时长
     */
    private int lockTime;

    /**
     * 是否开启双因子认证
     */
    private DoubleFactorType doubleFactor;
}

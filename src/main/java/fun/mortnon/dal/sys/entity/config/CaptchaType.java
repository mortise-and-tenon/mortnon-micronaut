package fun.mortnon.dal.sys.entity.config;

/**
 * 验证码类型
 *
 * @author dev2007
 * @date 2024/3/15
 */
public enum CaptchaType {
    /**
     * 未开启
     */
    DISABLE,

    /**
     * 图形算术验证码
     */
    ARITHMETIC,

    /**
     * 其他
     */
    OTHER,
}

package fun.mortnon.dal.sys.entity.config;

/**
 * 双因子类型
 *
 * @author dev2007
 * @date 2024/3/15
 */
public enum DoubleFactorType {
    /**
     * 未开启
     */
    DISABLE,

    /**
     * 邮箱
     */
    EMAIL,

    /**
     * 手机短信
     */
    PHONE,

    /**
     * 其他
     */
    OTHER,
}

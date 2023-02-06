package fun.mortnon.service.login.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * @author dongfangzan
 * @date 27.4.21 3:18 下午
 */
public enum LoginStorageType {

    /** redis 存储session */
    REDIS(LoginConstants.REDIS, "redis"),

    /** 内存存储 */
    LOCAL(LoginConstants.LOCAL, "本地内存");

    /** code */
    private final String code;

    /** 描述信息 */
    private final String description;

    LoginStorageType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据type获取枚举
     *
     * @param type type
     * @return     枚举
     */
    public static LoginStorageType getByType(String type) {
        if (StringUtils.isBlank(type)) {
            return LOCAL;
        }

        return Arrays.stream(LoginStorageType.values()).filter(typeEnum -> typeEnum.getCode().equals(type))
                .findAny().orElse(LOCAL);
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}

package fun.mortnon.framework.properties;

/**
 * @author dev2007
 * @date 2023/2/3
 */
public class JwtProperties {
    /**
     * token名称,默认名称为：token，可自定义
     */
    private String tokenName = "token";

    /**
     * 密码
     */
    private String secret = "666666";

    /**
     * 签发人
     */
    private String issuer;

    /**
     * 主题
     */
    private String subject;

    /**
     * 签发的目标
     */
    private String audience;

    /**
     * token失效时间,默认1小时，60*60=3600
     */
    private Long expireSecond = 60L*60L;

    /**
     * 是否刷新token，默认为true
     */
    private boolean refreshToken = true;

    /**
     * 刷新token倒计时，默认10分钟，10*60=600
     */
    private Integer refreshTokenCountdown;

    /**
     * 是否进行盐值校验
     */
    private boolean saltCheck;

    /**
     * 默认登录存储类型
     */
    private String loginStorageType;

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public Long getExpireSecond() {
        return expireSecond;
    }

    public void setExpireSecond(Long expireSecond) {
        this.expireSecond = expireSecond;
    }

    public boolean isRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(boolean refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Integer getRefreshTokenCountdown() {
        return refreshTokenCountdown;
    }

    public void setRefreshTokenCountdown(Integer refreshTokenCountdown) {
        this.refreshTokenCountdown = refreshTokenCountdown;
    }

    public boolean isSaltCheck() {
        return saltCheck;
    }

    public void setSaltCheck(boolean saltCheck) {
        this.saltCheck = saltCheck;
    }

    public String getLoginStorageType() {
        return loginStorageType;
    }

    public void setLoginStorageType(String loginStorageType) {
        this.loginStorageType = loginStorageType;
    }
}

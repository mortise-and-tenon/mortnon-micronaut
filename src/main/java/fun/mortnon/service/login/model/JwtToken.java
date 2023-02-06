package fun.mortnon.service.login.model;

import com.auth0.jwt.interfaces.DecodedJWT;
import fun.mortnon.framework.utils.JwtUtil;
import fun.mortnon.framework.web.MortnonContextHolder;
import fun.mortnon.service.login.enums.LoginType;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.HostAuthenticationToken;

import java.util.Date;

/**
 * @author dongfangzan
 * @date 27.4.21 2:57 下午
 */
@Slf4j
@Data
@Accessors(chain = true)
public class JwtToken implements HostAuthenticationToken {

    /** 登录ip */
    private String host;

    /** 用户名 */
    private String username;

    /** 登录盐 */
    private String salt;

    /** token */
    private String token;

    /** 创建时间 */
    private Date createTime;

    /** 过期时间 */
    private long expireSecond;

    /** 过期时间 */
    private Date expireTime;

    /** principal */
    private String principal;

    /** 验证 */
    private String credentials;

    /** 登录类型 */
    private LoginType loginType;

    /** 租户id */
    private String tenantId;

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    /**
     * 创建JwtToken
     *
     * @param token         token
     * @param username      用户名
     * @param salt          盐
     * @param expireSecond  过期时间
     * @return              token
     */
    public static JwtToken build(String token, String username, String salt, long expireSecond, LoginType loginType) {
        DecodedJWT decodedJwt = JwtUtil.getJwtInfo(token);
        Date createTime = decodedJwt.getIssuedAt();
        Date expireTime = decodedJwt.getExpiresAt();
        return new JwtToken()
                .setUsername(username)
                .setToken(token)
                .setHost("")
                .setSalt(salt)
                .setCreateTime(createTime)
                .setExpireSecond(expireSecond)
                .setExpireTime(expireTime)
                .setLoginType(loginType)
                .setTenantId(MortnonContextHolder.getTenantId());
    }
}

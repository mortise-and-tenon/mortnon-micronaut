package fun.mortnon.micronaut.pac4j.security;

import io.micronaut.security.authentication.Authentication;
import org.pac4j.core.profile.UserProfile;

import java.util.List;

/**
 * @author dev2007
 * @date 2023/3/3
 */
public interface AuthenticationGenerator {

    /**
     * 通过 UserProfile 转换为 Authentication 认证数据
     *
     * @param profile
     * @return
     */
    Authentication create(UserProfile profile);

    /**
     * 通过 List<UserProfile> 转换为 Authentication 认证数据
     *
     * @param profiles
     * @return
     */
    Authentication create(List<UserProfile> profiles);
}

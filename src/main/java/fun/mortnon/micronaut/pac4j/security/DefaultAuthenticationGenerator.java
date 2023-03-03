package fun.mortnon.micronaut.pac4j.security;

import io.micronaut.context.annotation.Requires;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.ServerAuthentication;
import jakarta.inject.Singleton;
import org.apache.commons.collections4.CollectionUtils;
import org.pac4j.core.profile.UserProfile;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author dev2007
 * @date 2023/3/3
 */
@Requires(beans = fun.mortnon.micronaut.pac4j.filter.CallbackFilter.class)
@Singleton
public class DefaultAuthenticationGenerator implements AuthenticationGenerator {
    @Override
    public Authentication create(UserProfile profile) {
        if (null == profile) {
            return null;
        }

        String username = profile.getUsername();
        Set<String> roles = profile.getRoles();
        Map<String, Object> attributes = profile.getAttributes();
        return new ServerAuthentication(username, roles, attributes);
    }

    @Override
    public Authentication create(List<UserProfile> profiles) {
        if (CollectionUtils.isEmpty(profiles)) {
            return null;
        }

        UserProfile profile = profiles.get(0);
        String username = profile.getUsername();
        Set<String> roles = profile.getRoles();
        Map<String, Object> attributes = profile.getAttributes();
        return new ServerAuthentication(username, roles, attributes);

    }
}

package fun.mortnon.service.login.security;

import com.nimbusds.jwt.JWTClaimsSet;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.runtime.ApplicationConfiguration;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.token.config.TokenConfiguration;
import io.micronaut.security.token.jwt.generator.claims.ClaimsAudienceProvider;
import io.micronaut.security.token.jwt.generator.claims.JWTClaimsSetGenerator;
import io.micronaut.security.token.jwt.generator.claims.JwtIdGenerator;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Map;

import static fun.mortnon.framework.constants.login.ClaimsConstants.PROJECT;

/**
 * JWT 生成器
 *
 * @author dev2007
 * @date 2023/2/7
 */
@Replaces(JWTClaimsSetGenerator.class)
@Singleton
@Slf4j
public class MortnonClaimsGenerator extends JWTClaimsSetGenerator {

    /**
     * @param tokenConfiguration       Token Configuration
     * @param jwtIdGenerator           Generator which creates unique JWT ID
     * @param claimsAudienceProvider   Provider which identifies the recipients that the JWT is intended for.
     * @param applicationConfiguration The application configuration
     */
    public MortnonClaimsGenerator(TokenConfiguration tokenConfiguration, @Nullable JwtIdGenerator jwtIdGenerator, @Nullable ClaimsAudienceProvider claimsAudienceProvider, @Nullable ApplicationConfiguration applicationConfiguration) {
        super(tokenConfiguration, jwtIdGenerator, claimsAudienceProvider, applicationConfiguration);
    }

    @Override
    public Map<String, Object> generateClaims(Authentication authentication, @Nullable Integer expiration) {
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
        populateIat(builder);
        populateExp(builder, expiration);
        populateJti(builder);
        populateIss(builder);
        populateAud(builder);
        populateNbf(builder);
        populateWithAuthentication(builder, authentication);
        populateProject(builder, authentication);
        return builder.build().getClaims();
    }

    private void populateProject(JWTClaimsSet.Builder builder, Authentication authentication) {
        Map<String, Object> attributes = authentication.getAttributes();
        builder.claim(PROJECT, attributes.getOrDefault(PROJECT, new HashSet<>()));
    }
}

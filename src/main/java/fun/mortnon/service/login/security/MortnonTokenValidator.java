package fun.mortnon.service.login.security;

import fun.mortnon.framework.properties.JwtProperties;
import fun.mortnon.service.login.security.impl.TokenPersistence;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.token.jwt.encryption.EncryptionConfiguration;
import io.micronaut.security.token.jwt.signature.SignatureConfiguration;
import io.micronaut.security.token.jwt.validator.GenericJwtClaimsValidator;
import io.micronaut.security.token.jwt.validator.JwtAuthenticationFactory;
import io.micronaut.security.token.jwt.validator.JwtTokenValidator;
import io.micronaut.security.token.jwt.validator.JwtValidator;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.util.Collection;

/**
 * @author dev2007
 * @date 2023/2/7
 */
@Replaces(JwtTokenValidator.class)
@Singleton
@Slf4j
public class MortnonTokenValidator extends JwtTokenValidator {
    @Inject
    private TokenPersistence tokenPersistence;

    public MortnonTokenValidator(Collection<SignatureConfiguration> signatureConfigurations, Collection<EncryptionConfiguration> encryptionConfigurations, Collection<GenericJwtClaimsValidator> genericJwtClaimsValidators, JwtAuthenticationFactory jwtAuthenticationFactory) {
        super(signatureConfigurations, encryptionConfigurations, genericJwtClaimsValidators, jwtAuthenticationFactory);
    }

    public MortnonTokenValidator(JwtValidator validator, JwtAuthenticationFactory jwtAuthenticationFactory) {
        super(validator, jwtAuthenticationFactory);
    }

    @Override
    public Publisher<Authentication> validateToken(String token, @Nullable HttpRequest<?> request) {
        //校验 token 在持久化存储中是否存在; 持久化处理的在 TokenListener 中实现
        if (tokenPersistence.isExists(token)) {
            return super.validateToken(token, request);
        }
        log.warn("token is not exist:{}.", token);
        return Flux.empty();
    }
}

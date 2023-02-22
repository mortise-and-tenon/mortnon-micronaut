package fun.mortnon.service.login.security;

import fun.mortnon.service.login.security.impl.TokenPersistence;
import io.micronaut.core.convert.value.MutableConvertibleValues;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.http.cookie.CookieConfiguration;
import io.micronaut.http.cookie.Cookies;
import io.micronaut.security.filters.SecurityFilter;
import io.micronaut.security.handlers.LogoutHandler;
import io.micronaut.security.token.jwt.bearer.BearerTokenReader;
import io.micronaut.session.Session;
import io.micronaut.session.http.HttpSessionFilter;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

import static fun.mortnon.framework.constants.CharConstants.SLASH;
import static fun.mortnon.framework.constants.MortnonConstants.TOKEN_KEY;

/**
 * 登出后处理
 *
 * @author dev2007
 * @date 2023/2/21
 */
@Singleton
public class AuthLogoutHandler implements LogoutHandler {

    @Inject
    private TokenPersistence tokenPersistence;

    @Inject
    private BearerTokenReader bearerTokenReader;

    @Override
    public MutableHttpResponse<?> logout(HttpRequest<?> request) {
        MutableHttpResponse<Object> response = HttpResponse.ok();
        clearCookie(request, response);
        clearPersistence(request);
        return response;
    }

    private String retrieveTokenFromCookie(HttpRequest<?> request) {
        return request.getCookies().findCookie(TOKEN_KEY).map(k -> k.getValue()).orElse("");
    }

    private void clearPersistence(HttpRequest<?> request) {
        String token = bearerTokenReader.findToken(request).orElseGet(() ->
                request.getCookies().findCookie(TOKEN_KEY).map(k -> k.getValue()).orElse("")
        );

        tokenPersistence.clearToken(token);
    }

    private void clearCookie(HttpRequest<?> request, MutableHttpResponse<?> response) {
        request.getCookies().getAll().stream()
                .filter(k -> TOKEN_KEY.equalsIgnoreCase(k.getName()))
                .map(c -> c.path(SLASH).value(""))
                .findAny().ifPresent(c -> response.cookie(c));
    }
}

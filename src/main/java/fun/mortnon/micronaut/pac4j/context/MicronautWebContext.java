package fun.mortnon.micronaut.pac4j.context;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.cookie.Cookies;
import org.pac4j.core.context.Cookie;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.WebContextHelper;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 适用于 Micronaut 的 WebContext 实现
 *
 * @author dev2007
 * @date 2023/3/1
 */
public class MicronautWebContext implements WebContext {
    private HttpRequest request;
    private MutableHttpResponse response;
    private String body;

    public MicronautWebContext(HttpRequest request, MutableHttpResponse response) {
        CommonHelper.assertNotNull("request", request);
        CommonHelper.assertNotNull("response", response);
        this.request = request;
        this.response = response;
    }

    public HttpRequest getNativeRequest() {
        return this.request;
    }

    /**
     * 获取 HttpResponse
     *
     * @return
     */
    public MutableHttpResponse getNativeResponse() {
        return this.response;
    }

    @Override
    public Optional<String> getRequestParameter(String name) {
        return Optional.ofNullable(this.request.getParameters().get(name));
    }

    @Override
    public Map<String, String[]> getRequestParameters() {
        return this.request.getParameters().asMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue().toArray(new String[entry.getValue().size()])));

    }

    @Override
    public Optional getRequestAttribute(String name) {
        return this.request.getAttribute(name);
    }

    @Override
    public void setRequestAttribute(String name, Object value) {
        this.request.setAttribute(name, value);
    }

    @Override
    public Optional<String> getRequestHeader(String name) {
        return Optional.ofNullable(this.request.getHeaders().get(name));
    }

    @Override
    public String getRequestMethod() {
        return this.request.getMethodName();
    }

    @Override
    public String getRemoteAddr() {
        return this.request.getRemoteAddress().getAddress().getHostAddress();
    }

    @Override
    public void setResponseHeader(String name, String value) {
        this.response.header(name, value);
    }

    @Override
    public Optional<String> getResponseHeader(String name) {
        return Optional.ofNullable(this.response.header(name));
    }

    @Override
    public void setResponseContentType(String content) {
        this.response.contentType(content);
    }

    @Override
    public String getServerName() {
        return this.request.getServerName();
    }

    @Override
    public int getServerPort() {
        return this.request.getServerAddress().getPort();
    }

    @Override
    public String getScheme() {
        return this.request.getUri().getScheme();
    }

    @Override
    public boolean isSecure() {
        return this.request.isSecure();
    }

    @Override
    public String getFullRequestURL() {
        String requestURL = request.getUri().toString();
        String queryString = request.getUri().getQuery();
        if (queryString == null) {
            return requestURL.toString();
        }
        return requestURL + "?" + queryString;
    }

    @Override
    public Collection<Cookie> getRequestCookies() {
        Collection<Cookie> pac4jCookies = new LinkedHashSet<>();
        Cookies cookies = this.request.getCookies();

        if (null != cookies) {
            cookies.getAll().forEach(c -> {
                Cookie cookie = new Cookie(c.getName(), c.getValue());
                cookie.setDomain(c.getDomain());
                cookie.setHttpOnly(c.isHttpOnly());
                cookie.setMaxAge(Long.valueOf(c.getMaxAge()).intValue());
                cookie.setPath(c.getPath());
                cookie.setSecure(c.isSecure());
                pac4jCookies.add(cookie);
            });
        }

        return pac4jCookies;
    }

    @Override
    public void addResponseCookie(Cookie cookie) {
        this.response.header("Set-Cookie", WebContextHelper.createCookieHeader(cookie));
    }

    @Override
    public String getPath() {
        String fullPath = request.getUri().getPath();

        if (fullPath == null) {
            return Pac4jConstants.EMPTY_STRING;
        }

        if (fullPath.startsWith("//")) {
            fullPath = fullPath.substring(1);
        }

        return fullPath;
    }

    @Override
    public String getRequestContent() {
        if (null == body) {
            body = (String) request.getBody().orElse(Pac4jConstants.EMPTY_STRING);
        }

        return body;
    }

    @Override
    public String getProtocol() {
        switch (request.getHttpVersion()) {
            case HTTP_1_1:
                return "Http/1.1";
            case HTTP_2_0:
                return "Http/2.0";
            case HTTP_1_0:
            default:
                return "Http/1.0";
        }
    }
}

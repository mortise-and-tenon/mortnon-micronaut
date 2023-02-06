package fun.mortnon.framework.utils;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.http.cookie.Cookies;
import io.micronaut.http.simple.cookies.SimpleCookie;

/**
 * cookie 工具
 *
 * @author dongfangzan
 * @date 20.4.21 5:53 下午
 */
public class CookieUtil {

    /**
     * 添加cookie
     *
     * @param response 响应
     * @param name      cookie key
     * @param value    cookie value
     * @param maxAge   超时时间
     */
    public static void addCookie(HttpResponse response, String name, String value, int maxAge) {
        Cookie cookie = new SimpleCookie(name, value);
        cookie.path("/");
        if (maxAge > 0) {
            cookie.maxAge(maxAge);
        }
        response.getCookies().getAll().add(cookie);
    }

    /**
     * 删除cookie
     *
     * @param response 响应
     * @param name     cookie name
     */
    public static void removeCookie(HttpResponse response, String name) {
        Cookie uid = new SimpleCookie(name, null);
        uid.path("/");
        uid.maxAge(0);
        response.getCookies().getAll().add(uid);
    }

    /**
     * 获取cookie值
     *
     * @param request 请求
     * @param name    cookie name
     * @return        cookie value
     */
    public static String getCookieValue(HttpRequest request, String name) {
        Cookies cookies = request.getCookies();
        // 这里一定要判空，不然可能导致大量报错影响页面性能
        if (null == cookies) {
            return null;
        }

        for (Cookie cookie : cookies.getAll()) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }

}

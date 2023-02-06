/*
 * Copyright 2019-2029 geekidea(https://github.com/geekidea)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fun.mortnon.framework.utils;

import fun.mortnon.framework.properties.JwtProperties;
import io.micronaut.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * JwtToken工具类
 *
 * @author geekidea
 * @date 2019-10-03
 * @since 1.3.0.RELEASE
 **/
@Slf4j
public class JwtTokenUtil {

    private static String tokenName;

    public JwtTokenUtil(JwtProperties jwtProperties) {
        tokenName = jwtProperties.getTokenName();
        log.debug("tokenName:{}", tokenName);
    }

    /**
     * 获取token名称
     *
     * @return
     */
    public static String getTokenName() {
        return tokenName;
    }

    /**
     * 从请求头或者请求参数中
     *
     * @return
     */
    public static String getToken() throws Exception {
        return getToken(WebUtil.getRequest());
    }

    /**
     * 从请求头或者请求参数中
     *
     * @param request
     * @return
     */
    public static String getToken(HttpRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request不能为空");
        }
        // 从请求头中获取token
        String token = request.getHeaders().get(tokenName);
        if (StringUtils.isBlank(token)) {
            // 从请求参数中获取token
            token = request.getParameters().get(tokenName);
        }
        if (StringUtils.isBlank(token)) {
            token = CookieUtil.getCookieValue(request, tokenName);
        }
        return token;
    }
}

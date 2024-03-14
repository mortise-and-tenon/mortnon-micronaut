package fun.mortnon.framework.aop;

import fun.mortnon.framework.properties.CommonProperties;
import fun.mortnon.framework.web.LogContextHolder;
import fun.mortnon.service.log.SysLogService;
import fun.mortnon.service.sys.SysUserService;
import fun.mortnon.web.vo.login.PasswordLoginCredentials;
import io.micronaut.aop.InterceptorBean;
import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.context.MessageSource;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.type.MutableArgumentValue;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.context.ServerRequestContext;
import io.micronaut.json.tree.JsonArray;
import io.micronaut.json.tree.JsonNode;
import io.micronaut.json.tree.JsonObject;
import io.micronaut.security.token.reader.TokenResolver;
import io.micronaut.security.token.validator.TokenValidator;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * {@link OperationLog} 注解的拦截器
 * 用于实现访问 API 时自动记录操作日志
 *
 * @author dev2007
 * @date 2023/3/7
 */
@Singleton
@InterceptorBean(OperationLog.class)
@Slf4j
public class OperationLogInterceptor implements MethodInterceptor<Object, Object> {
    @Inject
    private SysLogService operationLogService;

    @Inject
    private TokenResolver tokenResolver;

    @Inject
    private TokenValidator tokenValidator;

    @Inject
    private SysUserService sysUserService;

    @Inject
    private MessageSource messageSource;

    @Inject
    private CommonProperties commonProperties;

    @Inject
    private ObjectMapper mapper;

    @Nullable
    @Override
    public Object intercept(MethodInvocationContext<Object, Object> context) {
        String action = (String) context.getValue(OperationLog.class).orElseGet(() -> "");
        HttpRequest<Object> request = ServerRequestContext.currentRequest().orElse(null);

        Object result = context.proceed();

        String body = getRequestBody(request);

        String userName = context.getParameters().entrySet().stream().filter(entry -> {
            MutableArgumentValue<?> value = entry.getValue();
            return value.getValue() instanceof PasswordLoginCredentials;
        }).map(entry -> {
            PasswordLoginCredentials value = (PasswordLoginCredentials) entry.getValue().getValue();
            return value.getUsername();
        }).findAny().orElse("");

        LogContextHolder.setLogHolder(request, new LogContextHolder.LogData(userName, action, body));

        if (result instanceof Mono) {
            Mono<MutableHttpResponse<?>> responsePublisher = (Mono<MutableHttpResponse<?>>) result;
            return Mono.from(responsePublisher).map(response -> createLog(request, response));
        }

        Flux<MutableHttpResponse<?>> responsePublisher = (Flux<MutableHttpResponse<?>>) result;

        return Flux.from(responsePublisher).map(response -> createLog(request, response));
    }

    @NonNull
    private String getRequestBody(HttpRequest<Object> request) {

        JsonObject json = (JsonObject) request.getBody().orElse(null);
        if (ObjectUtils.isEmpty(json)) {
            return "";
        }

        List<String> paramPair = new ArrayList<>();
        json.entries().forEach(entry -> {
            String key = entry.getKey();
            String value = convertJsonNode(entry.getValue());

            //密码数据不记录在操作日志中
            if (key.contains("password")) {
                value = "*****";
            }
            paramPair.add("\"" + key + "\" : " + value);
        });

        return "{" + String.join(",", paramPair) + "}";
    }


    private String convertJsonNode(JsonNode node) {
        if (node.isString()) {
            return "\"" + node.getStringValue() + "\"";
        }
        if (node.isNumber()) {
            return node.getNumberValue().toString();
        }

        if (node.isArray()) {
            List<String> arrayList = new ArrayList<>();
            node.values().forEach(item -> {
                String value = convertJsonNode(item);
                arrayList.add(value);
            });

            return "[" + String.join(",", arrayList) + "]";
        }

        return "";
    }

    private MutableHttpResponse<?> createLog(HttpRequest<Object> request, MutableHttpResponse<?> response) {
        operationLogService.buildLog(request, response);
        return response;
    }


}

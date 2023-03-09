package fun.mortnon.framework.aop;

import fun.mortnon.dal.sys.entity.SysLog;
import fun.mortnon.dal.sys.entity.SysProject;
import fun.mortnon.service.log.SysLogService;
import fun.mortnon.service.log.SysLogBuilder;
import fun.mortnon.service.sys.SysUserService;
import fun.mortnon.web.vo.login.PasswordLoginCredentials;
import io.micronaut.aop.InterceptorBean;
import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.type.MutableArgumentValue;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.context.ServerRequestContext;
import io.micronaut.security.token.reader.TokenResolver;
import io.micronaut.security.token.validator.TokenValidator;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

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

    @Nullable
    @Override
    public Object intercept(MethodInvocationContext<Object, Object> context) {
        String action = (String) context.getValue(OperationLog.class).orElseGet(() -> "");
        HttpRequest<Object> request = ServerRequestContext.currentRequest().orElse(null);

        Object result = context.proceed();

        if (result instanceof Mono) {
            Mono<MutableHttpResponse<?>> responsePublisher = (Mono<MutableHttpResponse<?>>) result;
            return Mono.from(responsePublisher).flatMap(response -> afterPoint(context, request, response, action));
        }

        Flux<MutableHttpResponse<?>> responsePublisher = (Flux<MutableHttpResponse<?>>) result;

        return Flux.from(responsePublisher).flatMap(response -> afterPoint(context, request, response, action));
    }

    private Mono<? extends MutableHttpResponse<?>> afterPoint(MethodInvocationContext<Object, Object> context, HttpRequest<Object> request, MutableHttpResponse<?> response, String action) {
        SysLog sysLog = SysLogBuilder.build(request, response, action);
        return Flux.fromIterable(context.getParameters().entrySet())
                .filter(entry -> {
                    MutableArgumentValue<?> value = entry.getValue();
                    return value.getValue() instanceof PasswordLoginCredentials;
                })
                .map(entry -> {
                    PasswordLoginCredentials value = (PasswordLoginCredentials) entry.getValue().getValue();
                    return value.getUsername();
                })
                .collectList()
                .flatMap(authNameList -> {
                    if (CollectionUtils.isEmpty(authNameList)) {
                        String token = tokenResolver.resolveToken(request).orElse("");
                        if (StringUtils.isNotEmpty(token)) {
                            return Mono.from(tokenValidator.validateToken(token, request))
                                    .map(authentication -> {
                                        sysLog.setUserName(authentication.getName());
                                        return authentication.getName();
                                    })
                                    .flatMap(userName ->
                                            sysUserService.queryUserProject(userName)
                                                    .collect(Collectors.toSet())
                                    )
                                    .map(projectSet -> {
                                        for (SysProject project : projectSet) {
                                            sysLog.setProjectId(project.getId());
                                            sysLog.setProjectName(project.getName());
                                            break;
                                        }
                                        return sysLog;
                                    });
                        }
                    } else {
                        sysLog.setUserName(authNameList.get(0));
                    }

                    return Mono.just(sysLog);
                })
                .flatMap(logData -> {
                    if (StringUtils.isEmpty(sysLog.getUserName())) {
                        log.warn("no username,no operation log.");
                        return Mono.just(response);
                    }

                    return operationLogService.createLog(sysLog).map(syslog -> response);
                });
    }
}

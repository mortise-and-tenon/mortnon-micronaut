package fun.mortnon.service.login.security;

import fun.mortnon.dal.sys.entity.SysApiPermission;
import fun.mortnon.dal.sys.entity.SysPermission;
import fun.mortnon.dal.sys.entity.SysRole;
import fun.mortnon.dal.sys.entity.SysRolePermission;
import fun.mortnon.dal.sys.repository.ApiPermissionRepository;
import fun.mortnon.dal.sys.repository.PermissionRepository;
import fun.mortnon.dal.sys.repository.RolePermissionRepository;
import fun.mortnon.dal.sys.repository.RoleRepository;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.AntPathMatcher;
import io.micronaut.core.util.PathMatcher;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.rules.SecurityRuleResult;
import io.micronaut.web.router.RouteMatch;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * @author dev2007
 * @date 2023/2/9
 */
@Singleton
@Slf4j
public class PermissionRule implements SecurityRule {
    private final AntPathMatcher pathMatcher;
    private ApiPermissionRepository apiPermissionRepository;
    private RoleRepository roleRepository;
    private PermissionRepository permissionRepository;
    private RolePermissionRepository rolePermissionRepository;

    public PermissionRule(ApiPermissionRepository apiPermissionRepository, RoleRepository roleRepository, PermissionRepository permissionRepository,
                          RolePermissionRepository rolePermissionRepository) {
        this.pathMatcher = PathMatcher.ANT;
        this.apiPermissionRepository = apiPermissionRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    @Override
    public Publisher<SecurityRuleResult> check(HttpRequest<?> request, @Nullable RouteMatch<?> routeMatch, @Nullable Authentication authentication) {
        final String path = request.getUri().getPath();
        final HttpMethod httpMethod = request.getMethod();

        if (null == authentication) {
            return Mono.just(SecurityRuleResult.UNKNOWN);
        }

        Collection<String> roles = authentication.getRoles();
        if (CollectionUtils.isEmpty(roles)) {
            return Mono.just(SecurityRuleResult.REJECTED);
        }

        Predicate<SysApiPermission> predicate = p -> pathMatcher.matches(p.getApi(), path)
                && httpMethod.equals(p.getMethod());

        return Flux.fromIterable(roles)
                .flatMap(roleIdentifier -> roleRepository.findByIdentifier(roleIdentifier))
                .flatMap(role -> rolePermissionRepository.findByRoleId(role.getId()))
                .map(SysRolePermission::getPermissionId)
                .flatMap(pId -> permissionRepository.findById(pId))
                .map(SysPermission::getIdentifier)
                .flatMap(permission -> apiPermissionRepository.findAll().filter(predicate)
                        .filter(k -> k.getPermission().equals(permission)))
                .map(m -> SecurityRuleResult.ALLOWED)
                .defaultIfEmpty(SecurityRuleResult.REJECTED);
    }
}

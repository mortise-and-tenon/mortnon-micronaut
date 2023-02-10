package fun.mortnon.service.login;

import fun.mortnon.service.login.model.LoginUser;
import io.micronaut.security.authentication.AuthenticationRequest;
import reactor.core.publisher.Mono;

/**
 * @author dongfangzan
 * @date 27.4.21 3:09 下午
 */
public interface LoginService {

    /**
     * 认证后的登录，调用该方法完成最后的登录流程
     *
     * @param authenticationRequest 认证数据
     * @return 是否认证通过
     */
    Mono<Boolean> authenticate(AuthenticationRequest<?, ?> authenticationRequest);
}

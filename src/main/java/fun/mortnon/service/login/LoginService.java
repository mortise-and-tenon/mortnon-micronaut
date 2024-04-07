package fun.mortnon.service.login;

import fun.mortnon.service.login.model.LoginUser;
import fun.mortnon.web.vo.login.DoubleFactor;
import io.micronaut.http.HttpRequest;
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

    /**
     * 登录异常下是否要锁定
     *
     * @param request
     * @return 正数：锁定时长 <=0:锁定时长为0，无锁定
     */
    long interceptLogin(HttpRequest<?> request);

    /**
     * 生成双因子验证码
     *
     * @param doubleFactor
     * @return
     */
    boolean generateDoubleFactorCode(DoubleFactor doubleFactor);

    /**
     * 校验双因子验证码
     *
     * @param userName
     * @param code
     * @return
     */
    boolean verifyCode(String userName, String code);
}

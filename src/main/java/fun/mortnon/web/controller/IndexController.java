package fun.mortnon.web.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import reactor.core.publisher.Mono;

/**
 * @author dev2007
 * @date 2023/3/2
 */
@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/")
public class IndexController {

    @Get
    public Mono<Authentication> index(Authentication authentication) {
        return Mono.just(authentication);
    }
}

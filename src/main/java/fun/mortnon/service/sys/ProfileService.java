package fun.mortnon.service.sys;

import fun.mortnon.service.sys.vo.ProfileDTO;
import io.micronaut.core.annotation.Nullable;
import reactor.core.publisher.Mono;

import java.security.Principal;

/**
 * @author dev2007
 * @date 2024/3/11
 */
public interface ProfileService {
    Mono<ProfileDTO> queryProfile(@Nullable Principal principal);
}

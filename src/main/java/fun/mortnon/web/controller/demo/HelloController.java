package fun.mortnon.web.controller.demo;

import fun.mortnon.dal.sys.domain.SysUser;
import fun.mortnon.service.sys.SysUserService;
import fun.mortnon.web.entity.SysUserVo;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotBlank;
import java.net.URI;
import java.util.Optional;

@Controller("/hello")
public class HelloController {
    @Inject
    private SysUserService sysUserService;

    @Get("/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public String index(@NotBlank String name) {
        SysUser admin = sysUserService.getUserByUsername(name);
        return Optional.ofNullable(admin).map(t -> "" + t.getId() + t.getUserName()).orElse("no user");
    }

    @Post
    public Mono<HttpResponse<SysUser>> save() {
        SysUserVo sysUserVo = new SysUserVo();
        sysUserVo.setUserName("admin");
        sysUserVo.setNickName("admin user");
        sysUserVo.setPassword("123456");
        sysUserVo.setSalt("salt");
        sysUserVo.setSex(0);
        return sysUserService.createUser(sysUserVo).map(HelloController::createdUser);
    }

    @NonNull
    private static MutableHttpResponse<SysUser> createdUser(@NonNull SysUser sysUser) {
        return HttpResponse
                .created(sysUser);
    }


}

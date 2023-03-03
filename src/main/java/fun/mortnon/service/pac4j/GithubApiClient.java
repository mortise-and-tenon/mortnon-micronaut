package fun.mortnon.service.pac4j;

import io.micronaut.context.annotation.Parameter;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.client.annotation.Client;
import org.reactivestreams.Publisher;

/**
 * @author dev2007
 * @date 2023/2/28
 */
@Header(name = "User-Agent", value = "Micronaut")
@Client("https://gitee.com/api/v5")
public interface GithubApiClient {

    @Get("/user")
    Publisher<GithubUser> getUser(@Parameter("access_token") String authorization);
}

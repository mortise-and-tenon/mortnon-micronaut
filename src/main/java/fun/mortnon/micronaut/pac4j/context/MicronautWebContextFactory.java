package fun.mortnon.micronaut.pac4j.context;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.WebContextFactory;

/**
 * 从参数构建 Micronaut context.
 *
 * @author dev2007
 * @date 2023/3/1
 */
public class MicronautWebContextFactory implements WebContextFactory {
    public static final MicronautWebContextFactory INSTANCE = new MicronautWebContextFactory();

    @Override
    public MicronautWebContext newContext(Object... parameters) {
        HttpRequest request = (HttpRequest) parameters[0];
        MutableHttpResponse response = (MutableHttpResponse) parameters[1];
        return new MicronautWebContext(request, response);
    }
}

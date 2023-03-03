package fun.mortnon.micronaut.pac4j.http.adapter;

import fun.mortnon.micronaut.pac4j.context.MicronautWebContext;
import io.micronaut.http.MutableHttpResponse;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.WithContentAction;
import org.pac4j.core.exception.http.WithLocationAction;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.profile.factory.ProfileManagerFactoryAware;
import reactor.core.publisher.Mono;

import java.io.IOException;

/**
 * @author dev2007
 * @date 2023/3/1
 */
public class MicronautHttpActionAdapter implements HttpActionAdapter {
    public static final MicronautHttpActionAdapter INSTANCE = new MicronautHttpActionAdapter();

    @Override
    public Object adapt(HttpAction action, WebContext context) {
        if (action != null) {
            int code = action.getCode();
            MutableHttpResponse response = ((MicronautWebContext) context).getNativeResponse();

            response.status(code);

            if (action instanceof WithLocationAction) {
                final var withLocationAction = (WithLocationAction) action;
                context.setResponseHeader(HttpConstants.LOCATION_HEADER, withLocationAction.getLocation());

            } else if (action instanceof WithContentAction) {
                final var withContentAction = (WithContentAction) action;
                final var content = withContentAction.getContent();

                if (content != null) {
                    response.body(content);
                }
            }

            return response;
        }

        throw new TechnicalException("No action provided");
    }
}

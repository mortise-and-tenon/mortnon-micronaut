package fun.mortnon.micronaut.pac4j.context.session;

import io.micronaut.context.annotation.Prototype;
import io.micronaut.session.Session;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.context.session.SessionStoreFactory;

/**
 * 从参数构建 Micronaut session store.
 *
 * @author dev2007
 * @date 2023/3/1
 */

public class MicronautSessionStoreFactory implements SessionStoreFactory {

    private io.micronaut.session.SessionStore sessionStore;

    private Session session;

    private MicronautSessionStore micronautSessionStore;

    public MicronautSessionStoreFactory(io.micronaut.session.SessionStore sessionStore, Session session) {
        this.sessionStore = sessionStore;
        this.session = session;
        micronautSessionStore = new MicronautSessionStore(sessionStore, session);
    }


    @Override
    public SessionStore newSessionStore(Object... parameters) {
        return micronautSessionStore;
    }
}

package fun.mortnon.micronaut.pac4j.context.session;

import fun.mortnon.micronaut.pac4j.context.MicronautWebContext;
import io.micronaut.context.annotation.Prototype;
import io.micronaut.http.HttpRequest;
import io.micronaut.session.Session;
import io.micronaut.session.http.HttpSessionFilter;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author dev2007
 * @date 2023/3/1
 */
@Slf4j
public class MicronautSessionStore implements SessionStore {

    private io.micronaut.session.SessionStore<Session> sessionStore;

    private Session session;

    protected MicronautSessionStore() {
    }

    protected MicronautSessionStore(io.micronaut.session.SessionStore<Session> sessionStore, Session session) {
        this.sessionStore = sessionStore;
        this.session = session;
    }

    protected Optional<Session> getNativeSession(WebContext context, boolean createSession) {
        if (null != session) {
            log.debug("Provided session: {}", session);
            return Optional.of(session);
        } else {

            Session newSession = null;
            if (createSession) {
                newSession = sessionStore.newSession();
                sessionStore.save(newSession);
                log.debug("createSession: {}, retrieved session: {}", createSession, newSession);
            }
            return Optional.ofNullable(newSession);
        }
    }

    @Override
    public Optional<String> getSessionId(WebContext context, boolean createSession) {
        Optional<Session> nativeSession = getNativeSession(context, createSession);

        if (nativeSession.isPresent()) {
            String sessionId = nativeSession.get().getId();
            log.debug("Get sessionId: {}", sessionId);
            return Optional.of(sessionId);
        } else {
            log.debug("No sessionId");
            return Optional.empty();
        }
    }

    @Override
    public Optional<Object> get(WebContext context, String key) {
        Optional<Session> nativeSession = getNativeSession(context, false);
        if (nativeSession.isPresent()) {
            final var value = session.get(key);
            log.debug("Get value: {} for key: {}", value, key);
            return Optional.ofNullable(value);
        } else {
            log.debug("Can't get value for key: {}, no session available", key);
            return Optional.empty();
        }
    }

    @Override
    public void set(WebContext context, String key, Object value) {
        if (null == value) {
            Optional<Session> nativeSession = getNativeSession(context, false);
            if (nativeSession.isPresent()) {
                log.debug("Remove value for key: {}", key);
                nativeSession.get().remove(key);
            }
        } else {
            Optional<Session> nativeSession = getNativeSession(context, true);
            if (value instanceof Exception) {
                log.debug("Set key: {} for value: {}", key, value.toString());
            } else {
                log.debug("Set key: {} for value: {}", key, value);
            }
            nativeSession.get().put(key, value);
        }
    }

    @Override
    public boolean destroySession(WebContext context) {
        Optional<Session> nativeSession = getNativeSession(context, false);
        if (nativeSession.isPresent()) {
            Session session = nativeSession.get();
            log.debug("Invalidate session: {}", session);
            sessionStore.deleteSession(session.getId());
        }
        return true;
    }

    @Override
    public Optional<Object> getTrackableSession(WebContext context) {
        Optional<Session> nativeSession = getNativeSession(context, false);
        if (nativeSession.isPresent()) {
            Session session = nativeSession.get();
            log.debug("Return trackable session: {}", session);
            return Optional.of(session);
        } else {
            log.debug("No trackable session");
            return Optional.empty();
        }
    }

    @Override
    public Optional<SessionStore> buildFromTrackableSession(WebContext context, Object trackableSession) {
        if (trackableSession != null) {
            log.debug("Rebuild session from trackable session: {}", trackableSession);
            return Optional.of(new MicronautSessionStore(this.sessionStore, (Session) trackableSession));
        } else {
            log.debug("Unable to build session from trackable session");
            return Optional.empty();
        }
    }

    @Override
    public boolean renewSession(WebContext context) {
        Map<String, Object> attributes = new HashMap<>();
        Optional<Session> session = getNativeSession(context, false);
        if (session.isPresent()) {
            log.debug("Discard old session: {}", session.get().getId());
            attributes = session.get().asMap();
        }
        Session newSession = sessionStore.newSession();
        log.debug("And copy all data to the new one: {}", newSession.getId());
        attributes.forEach(newSession::put);
        return true;
    }
}

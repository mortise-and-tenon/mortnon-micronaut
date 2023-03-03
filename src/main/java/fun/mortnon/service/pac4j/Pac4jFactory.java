package fun.mortnon.service.pac4j;

import fun.mortnon.pac4j.oauth.client.GiteeClient;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;

/**
 * @author dev2007
 * @date 2023/3/1
 */
@Factory
public class Pac4jFactory {
    @Singleton
    public Config config() {
        GiteeClient giteeClient = new GiteeClient("da28980047eb2c732b8bcee4be567c6a4f38c6459587063f2607084c9c33b957",
                "4cd81eac1dae28b698044ed5b55e2580da94aca7d872e11e5b47d6c8a3b0a26d");
        Clients clients = new Clients("http://localhost:8080/callback", giteeClient);
        Config config = new Config(clients);
        return config;
    }
}

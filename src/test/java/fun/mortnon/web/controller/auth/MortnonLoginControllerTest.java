package fun.mortnon.web.controller.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.web.vo.login.PasswordLoginCredentials;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author dev2007
 * @date 2023/2/15
 */
@MicronautTest
public class MortnonLoginControllerTest {
    @Inject
    @Client("/")
    HttpClient client;

    private String token;

    @Test
    public void login() {
        PasswordLoginCredentials passwordLoginCredentials = new PasswordLoginCredentials();
        passwordLoginCredentials.setUsername("admin");
        passwordLoginCredentials.setPassword("123456");
        MutableHttpRequest<PasswordLoginCredentials> request = HttpRequest.POST("/login/password", passwordLoginCredentials);
        String body = client.toBlocking().retrieve(request, String.class);
        assertNotNull(body);
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(body);
            assertNotNull(jsonNode.get("access_token"));
            token = jsonNode.get("access_token").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void queryUser() {
        MutableHttpRequest<Object> request = HttpRequest.GET("/users");
        MortnonResult result = client.toBlocking().retrieve(request, MortnonResult.class);
        assertTrue(result.isSuccess());
    }
}
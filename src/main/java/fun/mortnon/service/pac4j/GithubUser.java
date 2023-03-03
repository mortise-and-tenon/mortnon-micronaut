package fun.mortnon.service.pac4j;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.micronaut.core.annotation.Introspected;
import lombok.Data;

/**
 * @author dev2007
 * @date 2023/2/28
 */
@Introspected
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Data
public class GithubUser {

    private String login;
    private String name;
    private String email;
}

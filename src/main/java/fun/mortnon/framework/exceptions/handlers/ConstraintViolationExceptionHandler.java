package fun.mortnon.framework.exceptions.handlers;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import fun.mortnon.framework.exceptions.NotFoundException;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.http.server.exceptions.response.ErrorResponseProcessor;
import io.micronaut.jackson.JacksonConfiguration;
import io.micronaut.validation.exceptions.ConstraintExceptionHandler;
import jakarta.inject.Singleton;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ElementKind;
import javax.validation.Path;
import java.util.Iterator;

/**
 * 数据校验异常处理器
 *
 * @author dev2007
 * @date 2023/3/13
 */
@Produces
@Singleton
@Requires(classes = {ConstraintViolationException.class})
@Replaces(ConstraintExceptionHandler.class)
public class ConstraintViolationExceptionHandler extends ConstraintExceptionHandler {

    private JacksonConfiguration jacksonConfiguration;

    public ConstraintViolationExceptionHandler(ErrorResponseProcessor<?> responseProcessor, JacksonConfiguration jacksonConfiguration) {
        super(responseProcessor);
        this.jacksonConfiguration = jacksonConfiguration;
    }

    @Override
    protected String buildMessage(ConstraintViolation violation) {
        Path propertyPath = violation.getPropertyPath();
        StringBuilder message = new StringBuilder();
        Iterator<Path.Node> i = propertyPath.iterator();

        while (i.hasNext()) {
            Path.Node node = i.next();

            if (node.getKind() != ElementKind.PARAMETER && node.getKind() != ElementKind.PROPERTY) {
                continue;
            }

            if (i.hasNext()) {
                node = i.next();
            }

            if (node.getIndex() != null) {
                message.append(String.format("[%d]", node.getIndex()));
            }

            PropertyNamingStrategy strategy = jacksonConfiguration.getPropertyNamingStrategy();
            String name = strategy.nameForField(null, null, node.getName());
            message.append(name);

        }

        message.append(": ").append(violation.getMessage());
        return message.toString();
    }
}

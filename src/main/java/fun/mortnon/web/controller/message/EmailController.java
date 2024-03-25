package fun.mortnon.web.controller.message;

import fun.mortnon.framework.message.MessageEvent;
import fun.mortnon.framework.message.entity.Message;
import fun.mortnon.framework.message.entity.MessageType;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author dev2007
 * @date 2024/3/21
 */
@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/test")
public class EmailController {
    @Inject
    private ApplicationEventPublisher publisher;

    @Get
    public MutableHttpResponse sendMail() {
        List<Long> toUsers = new ArrayList<>();
        toUsers.add(1L);
        String templateName = "VERIFY_CODE";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("code", "123456");

        Set<MessageType> types = new HashSet<>();
        types.add(MessageType.EMAIL);

        Message message = new Message();
        message.setMessageTypes(types);
        message.setReceiveUsers(toUsers);
        message.setTemplateName(templateName);
        message.setParameters(parameters);
        publisher.publishEvent(MessageEvent.publish(message));
        return HttpResponse.ok();
    }
}

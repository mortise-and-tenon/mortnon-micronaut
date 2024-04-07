package fun.mortnon.framework.message;

import fun.mortnon.framework.message.entity.Message;
import fun.mortnon.framework.message.entity.MessageType;
import fun.mortnon.service.sys.message.EmailService;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.ApplicationEventListener;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Set;

/**
 * 消息监听器
 * 用于接受消息事件，发送相应的通知消息
 *
 * @author dev2007
 * @date 2024/3/22
 */
@Requires(classes = MessageEvent.class)
@Singleton
@Slf4j
public class MessageListener implements ApplicationEventListener<MessageEvent> {
    @Inject
    private EmailService emailService;

    @Override
    public void onApplicationEvent(MessageEvent event) {
        Message message = event.getSource();
        if (ObjectUtils.isEmpty(message)) {
            log.warn("MessageEvent source is empty");
        }

        Set<MessageType> messageTypes = message.getMessageTypes();

        messageTypes.forEach(notice -> {
            switch (notice) {
                case EMAIL:
                    if (CollectionUtils.isNotEmpty(message.getReceiveUsers())) {
                        emailService.sendEmailToUser(message.getReceiveUsers(), message.getTemplateName(), message.getParameters());
                    } else if (CollectionUtils.isNotEmpty(message.getReceiveMailBox())) {
                        emailService.sendEmailToInbox(message.getReceiveMailBox(), message.getTemplateName(), message.getParameters());
                    }
                    break;
                case SMS:
                case ANNOUNCEMENT:
                default:
                    break;
            }
        });
    }
}

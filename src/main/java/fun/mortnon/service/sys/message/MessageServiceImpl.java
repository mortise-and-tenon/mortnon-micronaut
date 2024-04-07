package fun.mortnon.service.sys.message;

import fun.mortnon.dal.sys.entity.SysUser;
import fun.mortnon.framework.message.MessageEvent;
import fun.mortnon.framework.message.entity.Message;
import fun.mortnon.framework.message.entity.MessageType;
import fun.mortnon.service.sys.SysUserService;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.core.util.StringUtils;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fun.mortnon.framework.message.DbTemplateLoader.CUSTOM_THREAD_POOL;

/**
 * @author dev2007
 * @date 2024/3/29
 */
@Singleton
public class MessageServiceImpl implements MessageService {
    @Inject
    private SysUserService sysUserService;

    @Inject
    private ApplicationEventPublisher publisher;

    @Override
    public boolean sendCode(MessageType type, String userName, String templateName, Map<String, Object> parameters) {
        SysUser user = sysUserService.getUserByUsername(userName).block(Duration.ofSeconds(3));

        if (type == MessageType.EMAIL && StringUtils.isNotEmpty(user.getEmail())) {
            List<String> toEmails = new ArrayList<>();
            toEmails.add(user.getEmail());

            Set<MessageType> typeSet = new HashSet<>();
            typeSet.add(MessageType.EMAIL);
            Message notice = new Message();
            notice.setMessageTypes(typeSet);

            List<String> mailList = new ArrayList<>();
            mailList.add(user.getEmail());

            notice.setReceiveMailBox(mailList);
            notice.setTemplateName(templateName);
            notice.setParameters(parameters);

            publisher.publishEvent(MessageEvent.create(notice));
        }

        return true;
    }
}

package fun.mortnon.service.sys.message;

import fun.mortnon.framework.message.entity.MessageType;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * 信息服务
 *
 * @author dev2007
 * @date 2024/3/29
 */
public interface MessageService {
    boolean sendCode(MessageType type, String userName, String templateName, Map<String, Object> parameters);
}

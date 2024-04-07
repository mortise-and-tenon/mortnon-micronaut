package fun.mortnon.framework.message;

import fun.mortnon.framework.message.entity.Message;
import io.micronaut.context.event.ApplicationEvent;

/**
 * 消息事件
 * 只能通过 ApplicationEventPublisher 发送消息后，就能自动处理消息发送逻辑
 *
 * @author dev2007
 * @date 2024/3/22
 */
public class MessageEvent extends ApplicationEvent {
    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public MessageEvent(Object source) {
        super(source);
    }

    public Message getSource() {
        if (super.getSource() instanceof Message) {
            return (Message) super.getSource();
        }
        return null;
    }

    /**
     * 发布事件
     *
     * @param notice
     * @return
     */
    public static MessageEvent create(Message notice) {
        return new MessageEvent(notice);
    }
}

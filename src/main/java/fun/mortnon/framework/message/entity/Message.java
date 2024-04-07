package fun.mortnon.framework.message.entity;

import lombok.Data;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 通知消息数据内容
 *
 * @author dev2007
 * @date 2024/3/22
 */
@Data
public class Message {
    /**
     * 发送消息的方式
     */
    Set<MessageType> messageTypes = new HashSet<>();
    /**
     * 接收消息的用户 id 列表
     */
    private List<Long> receiveUsers;

    /**
     * 接收消息的邮箱
     */
    private List<String> receiveMailBox;

    /**
     * 消息主题（优先级高于模板）
     */
    private String subject;

    /**
     * 消息内容（优先级高级模板）
     */
    private String content;

    /**
     * 消息发布时间
     */
    private Instant publishDate;

    /**
     * 模板名字
     */
    private String templateName;

    /**
     * 模板要使用的参数数据
     */
    private Map<String, Object> parameters;
}

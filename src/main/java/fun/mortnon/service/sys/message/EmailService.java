package fun.mortnon.service.sys.message;

import java.util.List;
import java.util.Map;

/**
 * @author dev2007
 * @date 2024/3/21
 */
public interface EmailService {
    /**
     * 发送邮件
     *
     * @param toUsers
     * @param templateName
     * @param parameters
     */
    void sendEmail(List<Long> toUsers, String templateName, Map<String, Object> parameters);
}

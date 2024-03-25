package fun.mortnon.service.sys.message.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author dev2007
 * @date 2024/3/21
 */
@Data
@Accessors(chain = true)
public class Email {
    private String subject;
    private String content;
    private List<String> to = new ArrayList<>();
    private List<String> cc = new ArrayList<>();
    private List<String> bcc = new ArrayList<>();
}

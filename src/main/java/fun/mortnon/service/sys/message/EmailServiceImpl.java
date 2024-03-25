package fun.mortnon.service.sys.message;

import freemarker.template.Configuration;
import fun.mortnon.dal.sys.entity.SysTemplate;
import fun.mortnon.dal.sys.entity.SysUser;
import fun.mortnon.dal.sys.repository.TemplateRepository;
import fun.mortnon.dal.sys.repository.UserRepository;
import fun.mortnon.service.sys.message.entity.Email;
import fun.mortnon.service.sys.message.provider.MailSender;
import io.micronaut.core.util.StringUtils;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.io.StringWriter;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author dev2007
 * @date 2024/3/21
 */
@Singleton
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Inject
    private Configuration templateConfiguration;
    @Inject
    private MailSender mailSender;

    @Inject
    private TemplateRepository templateRepository;

    @Inject
    private UserRepository userRepository;

    @Override
    public void sendEmail(List<Long> toUsers, String templateName, Map<String, Object> parameters) {
        Email email = new Email();
        SysTemplate template = userRepository.findByIdInList(toUsers)
                .map(SysUser::getEmail)
                .collectList()
                .flatMap(emails -> {
                    List<String> emailList = emails.stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());
                    email.setTo(emailList);
                    return templateRepository.findByName(templateName);
                }).block(Duration.ofSeconds(3));

        email.setSubject(template.getSubject());
        try {
            StringWriter stringWriter = new StringWriter();
            templateConfiguration.getTemplate(templateName).process(parameters, stringWriter);
            email.setContent(stringWriter.toString());
        } catch (Exception e) {
            log.warn("Abnormal in creating email content:", e);
        }

        mailSender.send(email);
    }
}

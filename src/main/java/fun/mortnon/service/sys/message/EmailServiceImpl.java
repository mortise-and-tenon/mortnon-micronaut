package fun.mortnon.service.sys.message;

import freemarker.template.Configuration;
import fun.mortnon.dal.sys.entity.SysTemplate;
import fun.mortnon.dal.sys.entity.SysUser;
import fun.mortnon.dal.sys.entity.config.DoubleFactorType;
import fun.mortnon.dal.sys.repository.EmailConfigRepository;
import fun.mortnon.dal.sys.repository.TemplateRepository;
import fun.mortnon.dal.sys.repository.UserRepository;
import fun.mortnon.framework.enums.ErrorCodeEnum;
import fun.mortnon.framework.exceptions.NotFoundException;
import fun.mortnon.framework.exceptions.ParameterException;
import fun.mortnon.framework.exceptions.UsedException;
import fun.mortnon.framework.properties.CommonProperties;
import fun.mortnon.service.sys.ConfigService;
import fun.mortnon.service.sys.EncryptService;
import fun.mortnon.service.sys.message.entity.Email;
import fun.mortnon.service.sys.message.provider.MailSender;
import fun.mortnon.service.sys.vo.SysEmailConfigDTO;
import fun.mortnon.web.controller.system.command.message.TestEmailConfigCommand;
import fun.mortnon.web.controller.system.command.message.UpdateEmailConfigCommand;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.util.StringUtils;
import io.micronaut.runtime.context.scope.refresh.RefreshEvent;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.StringWriter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fun.mortnon.framework.constants.MessageConstants.VERIFY_CODE_PARAMETER_CODE;
import static fun.mortnon.framework.constants.MessageConstants.VERIFY_CODE_TEMPLATE;

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

    @Inject
    private EmailConfigRepository configRepository;

    @Inject
    private ApplicationEventPublisher eventPublisher;

    @Inject
    private EmailConfigRepository emailConfigRepository;

    @Inject
    private CommonProperties commonProperties;

    @Inject
    private EncryptService encryptService;

    @Inject
    private ConfigService configService;

    @PostConstruct
    public void init(){
        templateConfiguration.setInterpolationSyntax(Configuration.SQUARE_BRACKET_INTERPOLATION_SYNTAX);
    }

    @Override
    public Mono<SysEmailConfigDTO> queryConfig() {
        return configRepository.findById(1L)
                .map(SysEmailConfigDTO::convert);
    }

    @Override
    public Mono<SysEmailConfigDTO> saveEmailConfiguration(UpdateEmailConfigCommand configCommand) {
        //停用邮箱，需要判断双因子是否使用了
        if (!configCommand.isEnabled()) {
            return configService.queryConfig()
                    .flatMap(config -> {
                        DoubleFactorType doubleFactor = config.getDoubleFactor();
                        if (doubleFactor.equals(DoubleFactorType.EMAIL)) {
                            return Mono.error(UsedException.create(ErrorCodeEnum.USED_DATA_ERROR));
                        }
                        return Mono.just(true);
                    })
                    .map(result -> new SysEmailConfigDTO());
        }

        return emailConfigRepository.findById(1L)
                .flatMap(config -> {
                    if (StringUtils.isNotEmpty(configCommand.getHost())) {
                        config.setHost(configCommand.getHost());
                    }

                    if (ObjectUtils.isNotEmpty(configCommand.getPort())) {
                        config.setPort(configCommand.getPort().toString());
                    }

                    config.setEnabled(configCommand.isEnabled());

                    if (ObjectUtils.isNotEmpty(configCommand.getConnectionTimeout())) {
                        config.setConnectionTimeout(configCommand.getConnectionTimeout());
                    }

                    if (ObjectUtils.isNotEmpty(configCommand.getTimeout())) {
                        config.setTimeout(configCommand.getTimeout());
                    }

                    if (StringUtils.isNotEmpty(configCommand.getEmail())) {
                        config.setEmail(configCommand.getEmail());
                    }

                    if (ObjectUtils.isNotEmpty(configCommand.getHttps())) {
                        config.setHttps(configCommand.getHttps());
                    }

                    config.setAuth(configCommand.isAuth());

                    boolean authDataEmpty = configCommand.isAuth() &&
                            (StringUtils.isEmpty(configCommand.getUserName()) || StringUtils.isEmpty(configCommand.getPassword()));
                    if (authDataEmpty) {
                        return Mono.error(ParameterException.create(ErrorCodeEnum.DATA_EMPTY));
                    }

                    if (configCommand.isAuth()) {
                        config.setUserName(configCommand.getUserName());
                        config.setPassword(configCommand.getPassword());
                    }

                    //如果没配认证用的用户名和密码，认为不认证
                    if (org.apache.commons.lang3.StringUtils.isEmpty(configCommand.getUserName()) && org.apache.commons.lang3.StringUtils.isEmpty(configCommand.getPassword())) {
                        config.setAuth(false);
                    }

                    return Mono.just(config);
                })
                .flatMap(emailConfig -> emailConfigRepository.update(emailConfig))
                .map(config -> {
                    Map<String, Object> keys = Collections.singletonMap(MailSender.REFRESH_PREFIX, "*");
                    //更新配置后，刷新 MailSender 以更新配置
                    eventPublisher.publishEvent(new RefreshEvent(keys));
                    return config;
                })
                .map(SysEmailConfigDTO::convert);
    }

    @Override
    public void sendEmailToUser(List<Long> toUsers, String templateName, Map<String, Object> parameters) {
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

    @Override
    public void sendEmailToInbox(List<String> toEmails, String templateName, Map<String, Object> parameters) {
        Email email = new Email();
        email.setTo(toEmails);
        SysTemplate template = templateRepository.findByName(templateName).block(Duration.ofSeconds(3));

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

    @Override
    public Mono<Boolean> sendTestEmail(TestEmailConfigCommand testEmailConfigCommand, String userName) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(VERIFY_CODE_PARAMETER_CODE, testEmailConfigCommand.getCode());

        Email email = new Email();

        SysTemplate template = templateRepository.findByName(VERIFY_CODE_TEMPLATE).block(Duration.ofSeconds(3));

        email.setSubject(template.getSubject());
        try {
            StringWriter stringWriter = new StringWriter();
            templateConfiguration.getTemplate(VERIFY_CODE_TEMPLATE).process(parameters, stringWriter);
            email.setContent(stringWriter.toString());
        } catch (Exception e) {
            log.warn("Abnormal in creating email content:", e);
        }

        String pwd = encryptService.decryptByPrivateKey(testEmailConfigCommand.getPassword(), commonProperties.getSecret());
        if (StringUtils.isNotEmpty(pwd)) {
            testEmailConfigCommand.setPassword(pwd);
        }

        List<String> emailList = new ArrayList<>();

        return userRepository.findByUserName(userName)
                .flatMap(user -> {
                    if (StringUtils.isEmpty(user.getEmail())) {
                        return Mono.error(NotFoundException.create(ErrorCodeEnum.USER_INFO_ERROR));
                    }
                    emailList.add(user.getEmail());
                    email.setTo(emailList);

                    return Mono.just(email);
                })
                .map(mail -> mailSender.sendTest(email, testEmailConfigCommand));
    }
}

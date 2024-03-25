package fun.mortnon.service.sys.message.provider;

import fun.mortnon.dal.sys.entity.message.SysEmailConfig;
import fun.mortnon.dal.sys.repository.EmailConfigRepository;
import fun.mortnon.service.sys.message.entity.Email;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailConstants;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.Duration;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author dev2007
 * @date 2024/3/21
 */
@Singleton
@Slf4j
public class MailSender {
    private static final String MAIL_SMTP_SSL_TRUST = "mail.smtp.ssl.trust";

    private EmailConfigRepository emailConfigRepository;

    private Authenticator authenticator;

    private Session session;

    private String emailFrom;

    @Inject
    public MailSender(EmailConfigRepository emailConfigRepository) {
        this.emailConfigRepository = emailConfigRepository;
        init();
    }

    public void send(Email email) {
        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.setFrom(emailFrom);

            Address[] toAddress = new Address[email.getTo().size()];
            email.getTo().stream().map(item -> {
                try {
                    return new InternetAddress(item);
                } catch (AddressException e) {
                    return new InternetAddress();
                }
            }).collect(Collectors.toList()).toArray(toAddress);
            mimeMessage.setRecipients(MimeMessage.RecipientType.TO, toAddress);

            Address[] ccAddress = new Address[email.getCc().size()];
            email.getCc().stream().map(item -> {
                try {
                    return new InternetAddress(item);
                } catch (AddressException e) {
                    return new InternetAddress();
                }
            }).collect(Collectors.toList()).toArray(ccAddress);
            mimeMessage.setRecipients(MimeMessage.RecipientType.CC, ccAddress);

            Address[] bccAddress = new Address[email.getBcc().size()];
            email.getBcc().stream().map(item -> {
                try {
                    return new InternetAddress(item);
                } catch (AddressException e) {
                    return new InternetAddress();
                }
            }).collect(Collectors.toList()).toArray(bccAddress);
            mimeMessage.setRecipients(MimeMessage.RecipientType.BCC, bccAddress);

            mimeMessage.setSubject(email.getSubject());
            mimeMessage.setContent(email.getContent(), "text/html;charset=utf-8");
        } catch (MessagingException e) {
            log.warn("Exception in configuring email basic information: ", e);
        }

        try {
            Transport.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("Exception in sending emails: ", e);
        }
    }

    private void init() {
        SysEmailConfig config = emailConfigRepository.findById(1L).block(Duration.ofSeconds(3));
        emailFrom = config.getEmail();

        Properties properties = new Properties();
        if (!config.isEnabled()) {
            return;
        }

        properties.put(EmailConstants.MAIL_HOST, config.getHost());
        properties.put(EmailConstants.MAIL_PORT, config.getPort());
        properties.put(EmailConstants.MAIL_SMTP_CONNECTIONTIMEOUT, config.getConnectionTimeout());
        properties.put(EmailConstants.MAIL_SMTP_TIMEOUT, config.getTimeout());

        properties.put(EmailConstants.MAIL_SMTP_SOCKET_FACTORY_FALLBACK, "false");
        properties.put(MAIL_SMTP_SSL_TRUST, "*");

        switch (config.getHttps()) {
            case SSL:
                properties.put(EmailConstants.MAIL_SMTP_SSL_ENABLE, true);
                break;
            case TLS:
                properties.put(EmailConstants.MAIL_TRANSPORT_STARTTLS_ENABLE, true);
                break;
            default:
                break;
        }

        properties.put(EmailConstants.MAIL_SMTP_AUTH, config.isAuth());

        session = Session.getInstance(properties);
        if (config.isAuth()) {
            properties.put(EmailConstants.MAIL_SMTP_USER, config.getUserName());
            properties.put(EmailConstants.MAIL_SMTP_PASSWORD, config.getPassword());

            authenticator = new DefaultAuthenticator(config.getUserName(), config.getPassword());
            session = Session.getInstance(properties, authenticator);
            session.setDebug(config.isDebug());
        }
    }
}

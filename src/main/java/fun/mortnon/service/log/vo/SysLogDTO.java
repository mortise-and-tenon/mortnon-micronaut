package fun.mortnon.service.log.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fun.mortnon.dal.sys.entity.SysLog;
import fun.mortnon.framework.json.InstantSerializer;
import io.micronaut.context.MessageSource;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.Locale;
import java.util.Optional;

/**
 * @author dev2007
 * @date 2023/3/9
 */

@Introspected
@Serdeable
@Data
@Slf4j
public class SysLogDTO {
    /**
     * 日志 id
     */
    private Long id;

    /**
     * 国际化转义后的操作名字
     */
    private String action;

    /**
     * 操作用户名
     */
    @JsonProperty(value = "user_name")
    private String userName;

    /**
     * 组织名字
     */
    @JsonProperty(value = "project_name")
    private String projectName;

    /**
     * 操作者 ip
     */
    private String ip;

    /**
     * 国际化转义后的操作结果
     */
    private String result;

    /**
     * 国际化转义后操作级别
     */
    private String level;

    /**
     * 操作时间
     */
    @Serdeable.Serializable(using = InstantSerializer.class)
    private Instant time;

    public static SysLogDTO convert(SysLog sysLog, MessageSource messageSource, String lang) {
        if (null == messageSource) {
            log.warn("messageSource is null,can't convert SysLog to SysLogDTO.");
            return null;
        }

        if (StringUtils.isEmpty(lang)) {
            lang = "zh";
        }

        SysLogDTO sysLogDTO = new SysLogDTO();
        sysLogDTO.setId(sysLog.getId());
        sysLogDTO.setUserName(sysLog.getUserName());
        sysLogDTO.setIp(Optional.ofNullable(sysLog.getIp()).orElse(""));
        sysLogDTO.setProjectName(Optional.ofNullable(sysLog.getProjectName()).orElse(""));
        sysLogDTO.setTime(sysLog.getTime());

        sysLogDTO.setAction(i18n(messageSource, sysLog.getAction(), lang));
        sysLogDTO.setResult(i18n(messageSource, sysLog.getResult().getName(), lang));
        sysLogDTO.setLevel(i18n(messageSource, sysLog.getLevel().getName(), lang));

        return sysLogDTO;
    }

    private static String i18n(MessageSource messageSource, String code, String lang) {
        return messageSource.getMessage(code, MessageSource.MessageContext.of(new Locale(lang))).get();
    }
}

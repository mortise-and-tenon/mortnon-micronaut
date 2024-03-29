package fun.mortnon.framework.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 日期相关工具
 *
 * @author dev2007
 * @date 2024/2/28
 */
@Slf4j
public class DateTimeUtils {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 字符串转为 Instant
     *
     * @param dateStr
     * @return
     */
    public static Instant convert(String dateStr) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(dateStr, FORMATTER);
            return dateTime.atZone(ZoneId.systemDefault()).toInstant();
        } catch (DateTimeParseException e) {
            return Instant.EPOCH;
        }
    }

    /**
     * Instant 转为字符串
     *
     * @param date
     * @return
     */
    public static String convertStr(Instant date) {
        if (ObjectUtils.isEmpty(date)) {
            log.warn("DateTime convert date is empty.");
            return "";
        }
        return FORMATTER.format(LocalDateTime.ofInstant(date, ZoneId.systemDefault()));
    }
}

package fun.mortnon.service.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import fun.mortnon.dal.sys.entity.SysLog;
import fun.mortnon.dal.sys.entity.log.LogLevel;
import fun.mortnon.dal.sys.entity.log.LogResult;
import fun.mortnon.framework.utils.IpUtil;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.json.tree.JsonObject;
import jakarta.inject.Singleton;

import java.time.Instant;

/**
 * {@link SysLog} 构造器
 *
 * @author dev2007
 * @date 2023/3/8
 */
@Singleton
public class SysLogBuilder {

    public static SysLog build(HttpRequest request, int responseCode, String action, String body) {
        SysLog sysLog = new SysLog();
        sysLog.setAction(action);
        sysLog.setTime(Instant.now());
        sysLog.setRequest(body);

        if (null != request) {
            sysLog.setIp(IpUtil.getRequestIp(request));
        }

        if (responseCode >= 400) {
            sysLog.setLevel(LogLevel.WARN);
            sysLog.setResult(LogResult.FAILURE);
        } else {
            sysLog.setLevel(LogLevel.INFO);
            sysLog.setResult(LogResult.SUCCESS);
        }

        return sysLog;
    }
}

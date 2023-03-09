package fun.mortnon.service.log;

import fun.mortnon.dal.sys.entity.SysLog;
import fun.mortnon.dal.sys.entity.log.LogLevel;
import fun.mortnon.dal.sys.entity.log.LogResult;
import fun.mortnon.framework.utils.IpUtil;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
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
    public static SysLog build(HttpRequest request, HttpResponse response, String action) {
        SysLog sysLog = new SysLog();
        sysLog.setAction(action);
        sysLog.setTime(Instant.now());

        if (null != request) {
            sysLog.setIp(IpUtil.getRequestIp(request));
        }

        if (null != response) {
            int code = response.getStatus().getCode();

            if (code >= 400) {
                sysLog.setLevel(LogLevel.WARN);
                sysLog.setResult(LogResult.FAILURE);
            } else {
                sysLog.setLevel(LogLevel.INFO);
                sysLog.setResult(LogResult.SUCCESS);
            }
        }

        return sysLog;
    }
}

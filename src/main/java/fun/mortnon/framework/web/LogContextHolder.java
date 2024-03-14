package fun.mortnon.framework.web;

import io.micronaut.http.HttpRequest;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存请求相应的数据
 * 保证AOP写入操作日志的逻辑中，当业务使用异常中断时也能正确输出日志
 *
 * @author dev2007
 * @date 2024/3/14
 */
public class LogContextHolder {

    @Data
    public static class LogData {
        public LogData() {

        }

        public LogData(String userName, String action, String body) {
            this.userName = userName;
            this.action = action;
            this.body = body;
        }

        private String action;
        private String userName;
        private String body;
    }

    private static final Map<HttpRequest, LogData> ACTION_HOLDER = new ConcurrentHashMap<>();

    public static void setLogHolder(HttpRequest request, LogData logData) {
        ACTION_HOLDER.put(request, logData);
    }

    public static LogData getLogHolder(HttpRequest request) {
        return ACTION_HOLDER.getOrDefault(request, null);
    }

    public static void clearLogHolder(HttpRequest request) {
        ACTION_HOLDER.remove(request);
    }
}

package fun.mortnon.web.filter.model;

import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpStatus;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;

/**
 * @author dev2007
 * @date 2023/3/7
 */
@Data
@Slf4j
public class TraceLog {
    private static final String TRACE_FORMAT = "[{} - {} - {}][IP:{},Time:{} - {},Cost:{}ms]";

    private HttpMethod method;
    private String ip;
    private URI uri;
    private Instant begin;
    private Instant end;
    private HttpStatus status;


    public TraceLog() {

    }

    public TraceLog(String ip, HttpMethod method, URI uri) {
        this.ip = ip;
        this.method = method;
        this.uri = uri;
        this.begin = Instant.now();
    }

    public void setStatus(HttpStatus httpStatus) {
        this.status = httpStatus;
        this.end = Instant.now();
    }

    /**
     * 打印请求完整日志
     */
    public void print() {
        log.info(TRACE_FORMAT, this.method, this.uri, this.status, this.ip, this.begin, this.end, Duration.between(this.begin, this.end).toMillis());
    }
}

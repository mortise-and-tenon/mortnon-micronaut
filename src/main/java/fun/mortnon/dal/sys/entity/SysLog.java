package fun.mortnon.dal.sys.entity;

import fun.mortnon.dal.sys.entity.log.LogLevel;
import fun.mortnon.dal.sys.entity.log.LogResult;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

import java.time.Instant;


/**
 * @author dev2007
 * @date 2023/3/7
 */
@MappedEntity
@Data
@Serdeable
public class SysLog {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(GeneratedValue.Type.AUTO)
    private Long id;

    /**
     * 操作
     */
    private String action;

    /**
     * 操作的描述
     */
    private String actionDesc;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 组织名
     */
    private String projectName;

    /**
     * 组织 id
     */
    private Long projectId;

    /**
     * 访问 ip
     */
    private String ip;

    /**
     * 操作请求数据
     */
    private String request;

    /**
     * 操作响应的消息
     */
    private String message;

    /**
     * 操作结果
     */
    private LogResult result;

    /**
     * 日志级别
     */
    private LogLevel level;

    /**
     * 操作时间
     */
    private Instant time;

    public SysLog() {

    }
}

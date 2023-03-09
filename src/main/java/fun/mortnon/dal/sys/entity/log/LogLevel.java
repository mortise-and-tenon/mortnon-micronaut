package fun.mortnon.dal.sys.entity.log;

/**
 * @author dev2007
 * @date 2023/3/7
 */
public enum LogLevel {
    INFO("log.level.info"),
    WARN("log.level.warn"),
    DANGER("log.level.danger");

    private String name;

    LogLevel(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

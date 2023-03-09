package fun.mortnon.dal.sys.entity.log;

/**
 * @author dev2007
 * @date 2023/3/7
 */
public enum LogResult {
    SUCCESS("log.result.success"),
    FAILURE("log.result.failure");

    private String name;

    LogResult(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

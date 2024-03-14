package fun.mortnon.framework.constants;

/**
 * 操作日志 action 国际化 key 变量定义
 * <p>
 * 用于表记录，并在操作日志接口中用于 i18n 转换
 *
 * @author dev2007
 * @date 2023/3/9
 */
public class LogConstants {
    /**
     * 登录操作
     */
    public static final String LOGIN = "log.action.login";

    /**
     * 登出操作
     */
    public static final String LOGOUT = "log.action.logout";

    /**
     * 创建用户操作
     */
    public static final String USER_CREATE = "log.action.user.create";

    /**
     * 修改用户操作
     */
    public static final String USER_UPDATE = "log.action.user.update";

    /**
     * 删除用户操作
     */
    public static final String USER_DELETE = "log.action.user.delete";

    /**
     * 修改用户密码操作
     */
    public static final String USER_PASSWORD_UPDATE = "log.action.user.password.update";

    /**
     * 用户导入
     */
    public static final String USER_IMPORT = "log.action.user.import";

    /**
     * 变更用户状态
     */
    public static final String USER_STATUS_UPDATE = "log.action.user.status.update";

    /**
     * 创建组织操作
     */
    public static final String PROJECT_CREATE = "log.action.project.create";

    /**
     * 修改组织操作
     */
    public static final String PROJECT_UPDATE = "log.action.project.update";

    /**
     * 删除组织操作
     */
    public static final String PROJECT_DELETE = "log.action.project.delete";

    /**
     * 创建角色操作
     */
    public static final String ROLE_CREATE = "log.action.role.create";

    /**
     * 修改角色操作
     */
    public static final String ROLE_UPDATE = "log.action.role.update";

    /**
     * 删除角色操作
     */
    public static final String ROLE_DELETE = "log.action.role.delete";

    /**
     * 分配用户角色、组织操作
     */
    public static final String ASSIGNMENT_CREATE = "log.action.assignment.create";

    /**
     * 撤销分配用户角色、组织操作
     */
    public static final String ASSIGNMENT_DELETE = "log.action.assignment.delete";

    public static final String MENU_CREATE = "log.action.menu.create";

    public static final String MENU_UPDATE = "log.action.menu.update";

    public static final String MENU_DELETE = "log.action.menu.delete";
}

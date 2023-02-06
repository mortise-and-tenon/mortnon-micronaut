package fun.mortnon.framework.web;

/**
 * 上下文工具
 *
 * @author dongfangzan
 * @date 20.4.21 2:58 下午
 */
public class MortnonContextHolder {

    private static final ThreadLocal<String> CURRENT_TENANT_ID = new ThreadLocal<>();

    /**
     * 设置当前的租户id
     *
     * @param tenantId 租户id
     */
    public static void setTenantId(String tenantId) {
        CURRENT_TENANT_ID.set(tenantId);
    }

    /**
     * 获取当前的租户id
     *
     * @return 租户id
     */
    public static String getTenantId() {
        return CURRENT_TENANT_ID.get();
    }

    /**
     * 清理租户
     */
    public static void clear() {
        CURRENT_TENANT_ID.remove();
    }
}

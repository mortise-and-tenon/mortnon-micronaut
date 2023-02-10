package fun.mortnon.service.sys.vo;

import lombok.Data;

/**
 * @author dev2007
 * @date 2023/2/9
 */
@Data
public class ApiPermissionDTO {
    private String path;
    private String method;
    private String permission;
}

package fun.mortnon.service.sys.impl;

import fun.mortnon.dal.sys.domain.SysUser;
import fun.mortnon.service.sys.SysUserService;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author dev2007
 * @date 2023/2/3
 */
@MicronautTest
class SysUserServiceImplTest {
    @Inject
    SysUserService sysUserService;

    @Test
    void getUserByUsername() {
        SysUser admin = sysUserService.getUserByUsername("admin");
        Assertions.assertNotNull(admin);
    }
}
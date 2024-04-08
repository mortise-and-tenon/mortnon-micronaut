## Mortnon Micronaut

Mortnon 实现常见的 Java Web 项目所必需的特性，如 RBAC 等，支持响应式编程，支持 GraalVM Native Image 编译，性能极佳。

项目地址：
- [GitHub](https://github.com/mortise-and-tenon/mortnon-micronaut)
- [Gitee](https://gitee.com/mortise-and-tenon/mortnon-with-micronaut)

### Micronaut

Mortnon 基于 Micronaut 实现。

- [User Guide](https://docs.micronaut.io/3.8.7/guide/index.html)
- [API Reference](https://docs.micronaut.io/3.8.7/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/3.8.7/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)
---

### 框架核心特性

1. RBAC：用户管理、角色管理、组织管理
2. 菜单管理：自定义菜单
3. 日志管理：操作审计
4. 消息中心：配置电子邮件服务器等
5. 安全设置：图形验证码、敏感数据加密传输、失败锁定、双因子登录

### 编译部署

1. 支持编译为 Jar，大小约 55M，远远小于 Spring Boot 框架
2. 支持编译为 Docker 镜像
3. 支持编译为本地应用（Native Image）的 Docker 镜像，性能与本地应用一致，大小远小于 Jar

详情参阅 [编译指南](./guide/编译指南.md) 

### 扩展目标
1. 支持 Pac4j 扩展 [Micronaut Pac4j](https://github.com/mortise-and-tenon/micronaut-pac4j)

### 前端

配套前端项目

- [GitHub](https://github.com/mortise-and-tenon/mortnon-micronaut-web)
- [Gitee](https://gitee.com/mortise-and-tenon/mortnon-web)

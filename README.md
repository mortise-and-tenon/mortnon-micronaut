## Mornton 

Mornton 实现常见的 Java Web 项目所必需的特性，如 RBAC 等，并最终能方便的支持容器化等特性。

### Micronaut

Mornton 基于 Micronaut 实现。

- [User Guide](https://docs.micronaut.io/3.8.7/guide/index.html)
- [API Reference](https://docs.micronaut.io/3.8.7/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/3.8.7/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)
---

### 实现目标

1. √ 基本的框架迁移（Spring Boot -> Micronaut）
2. √ 过滤器实现 API 访问日志
3. √ AOP 实现操作日志 
4. √ RBAC（用户管理、角色及权限管理、组织管理，用户可分配角色和组织） 
5. √ 提供认证及鉴权（Token 基于 JWT，鉴权传输支持请求头和 Cookie）

### 同步目标
1. √ Pac4j 扩展 [Micronaut Pac4j](https://github.com/mortise-and-tenon/micronaut-pac4j)
2. √ 编译为 GraalVM Native Image



# 使用说明

## 编译指南

项目使用 Java 语言开发，使用 Maven 进行包管理，如果需要进行编译，环境需要安装有 JDK 11 和 Maven。

如果要将项目编译为 GraalVM 支持的 Native Image，JDK 需要使用 GraalVM，安装指南：https://www.graalvm.org/latest/docs/getting-started/linux/

如果要将项目编译为 Docker 镜像，环境还需要安装有 Docker，安装指南参考：https://docs.docker.com/engine/install/centos/

### 一、源码获取（通过 Git）

新建一个目录，进入目录，克隆仓库。命令如下：

- GitHub
- 
```bash
git clone https://github.com/mortise-and-tenon/mortnon-micronaut.git
```

- Gitee

```bash
git clone https://gitee.com/mortise-and-tenon/mortnon-micronaut.git
```

> 以下的编译没有依赖关系，自行选择最喜欢的。

### 二、编译 Jar

> 直接编译一个 Jar，就可以运行项目了。

#### Windows 环境

- 直接使用 IDEA 工具中的 Maven 选项 `package` 即可打包出 jar。
- 使用 CMD 或 PowerShell 切换到源码所在文件夹中，使用命令如下：

```bash
mvn clean -DskipTests package
```

#### Linux 环境

切换到源码所在的文件夹中，使用命令如下：

```bash
mvn clean -DskipTests package
```

编译成功后，在源码所在文件夹的 `target` 目录中看到编译成功的 jar: `mortnon-micronaut-X.X.jar`，大小约 55M。

### 三、编译 Native Image

**暂不支持**

> Micronaut 框架支持 GraalVM 的 Native Image 编译，可以将 Java 应用编译为当前机器对应的本地应用，应用容量更小，运行时性能更高、占用内存更小。

```bash
mvn package -Dpackaging=native-image
```

### 四、编译 Docker 镜像

> 如果需要将应用容器化，可以使用以下命令直接将 Jar 制作为 Docker 镜像。Micronaut 框架的 Maven 插件自带全套步骤，在编译时自动生成 Dockerfile，最终直接得到一个可运行的 Docker 镜像。
> 如果想自己定义 Docker 镜像，建议自行编写 Dockerfile，在编译为 Jar 后自行处理后续镜像生成。 

```bash
mvn package -Dpackaging=docker
```

### 五、编译 Native Image 的 Docker 镜像

**暂不支持，基础镜像无freetype**

> 如果需要一个本地应用的 Docker 镜像，可以直接使用以下命令。
> 注意：编译的本地应用依赖当前编译环境的机器环境一致，如果需要国产化/信创的编译，请在相应的国产服务器上进行编译，并修改 `pom.xml` 配置适合的 Docker 基础镜像。

```bash
mvn package -Dpackaging=docker-native
```

### 六、自定义编译 Native Image Docker 镜像


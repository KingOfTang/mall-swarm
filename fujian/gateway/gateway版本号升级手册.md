从 3.1.3 升级到 3.1.10 属于小版本升级，通常比较平滑。在 mall-swarm 项目中，你不需要直接修改 gateway 模块的依赖。由于这个项目使用了统一的父 pom.xml 管理依赖版本，你只需修改一个地方。

### **升级步骤 🛠️**

#### **1\. 修改父 pom.xml 文件**

spring-cloud-starter-gateway 的版本号是由父 pom.xml 中的 spring-cloud.version 属性决定的。因此，你只需要修改项目根目录下的 pom.xml 文件。

找到 \<properties\> 部分，将 spring-cloud.version 的值从 2021.0.3 升级到与 3.1.10 版本兼容的最新 spring-cloud 版本。

XML

\<properties\>  
    \<project.build.sourceEncoding\>UTF-8\</project.build.sourceEncoding\>  
    \<project.reporting.outputEncoding\>UTF-8\</project.reporting.outputEncoding\>  
    \<java.version\>1.8\</java.version\>  
    \<skipTests\>true\</skipTests\>  
    \<spring-cloud.version\>2021.0.5\</spring-cloud.version\>  
\</properties\>

spring-cloud-starter-gateway:3.1.10 依赖于 spring-cloud:2021.0.5。因此，将 spring-cloud.version 升级到 2021.0.5 就能自动拉取 3.1.10 版本的 Gateway。

#### **2\. 重新加载 Maven 依赖**

修改并保存父 pom.xml 文件后，你需要在 IDE（如 IntelliJ IDEA）中重新加载 Maven 依赖，或者在命令行中执行 mvn clean install。这会下载新版本的依赖并更新项目的本地仓库。

#### **3\. 重新打包和部署**

依赖更新后，你需要重新打包 mall-gateway 模块，然后重新构建 Docker 镜像，并部署到你的服务器上。

Bash

\# 在 mall-gateway 项目目录下  
mvn clean package \-DskipTests

\# 重新构建 Docker 镜像  
docker build \-t mall/mall-gateway:1.0-SNAPSHOT .

\# 重新启动服务  
docker-compose \-f docker-compose-app.yml up \--force-recreate \-d

### **总结**

对于 mall-swarm 项目，将 Gateway 从 3.1.3 升级到 3.1.10 只需要一个步骤：**修改根 pom.xml 中的 spring-cloud.version**。

小版本升级通常不会引入破坏性更改，但如果你在升级后遇到任何问题，请查看 Gateway 的发布说明（Release Notes），以了解是否有任何配置或行为上的细微变化。
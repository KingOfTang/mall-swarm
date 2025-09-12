我已为你整理好 spring-cloud.version 为 2021.0.x 系列与 spring-cloud-starter-gateway 的准确版本映射关系。

---

### **Spring Cloud 2021.0.x (Jubilee) 与 Gateway 版本映射**

Spring Cloud 2021.0.x 是一个长期支持版本，它与 Spring Boot 2.6.x 和 2.7.x 版本兼容。在这个版本系列中，spring-cloud-starter-gateway 的版本号都以 3.1.x 开头。

下面是详细的映射关系：

| spring-cloud.version           | spring-cloud-starter-gateway 版本 |
|:-------------------------------|:-------------------------------|
| 2021.0.0                       | 3.1.0                          |
| 2021.0.1                       | 3.1.1                          |
| 2021.0.2                       | 3.1.2                          |
| 2021.0.3                       | 3.1.3                          |
| 2021.0.4                       | 3.1.4                          |
| 2021.0.5                       | 3.1.4                          |
| 2021.0.6                       | 3.1.6                          |
| 2021.0.7                       | 3.1.7                          |
| 2021.0.8                       | 3.1.8                          |
| 2021.0.9                       | 3.1.9                          |

### **如何确定你项目中的 Gateway 版本**

在 mall-swarm 项目中，你需要检查项目根目录下的父 pom.xml 文件，找到 \<properties\> 部分的 \<spring-cloud.version\> 标签。

例如，如果你的配置是：

XML

\<properties\>  
    \<spring-cloud.version\>2021.0.3\</spring-cloud.version\>  
\</properties\>

根据上面的表格，你的项目中 spring-cloud-starter-gateway 的版本就是 **3.1.3**。

要获取更精确的版本信息，最准确的方法是在 mall-gateway 模块的目录下运行以下 Maven 命令：

Bash

mvn dependency:tree | grep spring-cloud-starter-gateway

这会直接返回依赖的确切版本，例如：

\[INFO\] |  \+- org.springframework.cloud:spring-cloud-starter-gateway:jar:3.1.3:compile

这里的 **3.1.3** 就是你项目中 Gateway 的版本号。
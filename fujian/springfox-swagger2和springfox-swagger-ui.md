我明白你的困惑。在 mall-swarm 项目中，你可能没有直接在每个服务的 pom.xml 文件中看到 springfox-swagger2 和 springfox-swagger-ui 这两个依赖。

这是因为它们被统一管理在项目的**父 pom.xml** 文件中，或者被包含在了一个公共模块（如 mall-common）中，然后其他服务再依赖这个公共模块。

---

### **作用和原理**

* **springfox-swagger2**: 它的作用是**自动扫描代码**。它会读取你用 @Api、@ApiOperation 等注解标记的控制器和方法，然后根据这些信息自动生成一份标准的 API 文档（JSON 格式）。  
* **springfox-swagger-ui**: 它的作用是**提供界面**。它是一个 Web 界面，可以解析 springfox-swagger2 生成的 JSON 文档，并以一个友好、可交互的网页形式展示出来。你可以直接在这个网页上测试和调用 API 接口。

简单来说，**springfox-swagger2 是大脑，负责生成内容；springfox-swagger-ui 是界面，负责把内容展示出来。** 它们一起工作，让你的 API 文档变得自动化且易于使用。
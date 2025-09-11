![img.png](img.png)
这段配置的作用是：

它告诉 Spring MVC，使用 ant_path_matcher 这种路径匹配策略来处理 URL 请求。

简单来说，就是让你的程序能够理解和匹配带有 通配符 的路径，比如：

/user/* 可以匹配 /user/1 或 /user/a

/user/** 可以匹配 /user/info 或 /user/1/details

这种策略在 Spring Boot 2.6.x 之前的版本是默认的。如果你在较新的版本中看到它，通常是为了兼容旧项目。


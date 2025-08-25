package com.macro.mall.component;

import com.macro.mall.service.UmsResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 资源与角色访问对应关系操作组件
 * 该Java代码定义了一个名为[UmsResourceService]的接口，用于管理后台资源的相关操作，包括：
 *
 * - 添加、修改、删除和查询资源（按ID获取详情）
 * - 支持分页查询资源（可按分类、名称和URL关键字筛选）
 * - 查询全部资源
 * - 初始化资源与角色的映射关系
 *
 * 该接口为面向服务层设计，具体逻辑由实现类完成。
 */
@Component
public class ResourceRoleRulesHolder {

    @Autowired
    private UmsResourceService resourceService;

    @PostConstruct
    public void initResourceRolesMap(){
        resourceService.initResourceRolesMap();
    }
}

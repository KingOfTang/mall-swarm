package com.macro.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.macro.mall.common.constant.AuthConstant;
import com.macro.mall.common.service.RedisService;
import com.macro.mall.mapper.UmsResourceMapper;
import com.macro.mall.mapper.UmsRoleMapper;
import com.macro.mall.mapper.UmsRoleResourceRelationMapper;
import com.macro.mall.model.*;
import com.macro.mall.service.UmsAdminCacheService;
import com.macro.mall.service.UmsResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 后台资源管理Service实现类
 * Created by macro on 2020/2/2.
 */
@Service
public class UmsResourceServiceImpl implements UmsResourceService {
    @Autowired
    private UmsResourceMapper resourceMapper;
    @Autowired
    private UmsRoleMapper roleMapper;
    @Autowired
    private UmsRoleResourceRelationMapper roleResourceRelationMapper;
    @Autowired
    private RedisService redisService;
    @Value("${spring.application.name}")
    private String applicationName;
    @Override
    public int create(UmsResource umsResource) {
        umsResource.setCreateTime(new Date());
        int count = resourceMapper.insert(umsResource);
        initResourceRolesMap();
        return count;
    }

    @Override
    public int update(Long id, UmsResource umsResource) {
        umsResource.setId(id);
        int count = resourceMapper.updateByPrimaryKeySelective(umsResource);
        initResourceRolesMap();
        return count;
    }

    @Override
    public UmsResource getItem(Long id) {
        return resourceMapper.selectByPrimaryKey(id);
    }

    @Override
    public int delete(Long id) {
        int count = resourceMapper.deleteByPrimaryKey(id);
        initResourceRolesMap();
        return count;
    }

    @Override
    public List<UmsResource> list(Long categoryId, String nameKeyword, String urlKeyword, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum,pageSize);
        UmsResourceExample example = new UmsResourceExample();
        UmsResourceExample.Criteria criteria = example.createCriteria();
        if(categoryId!=null){
            criteria.andCategoryIdEqualTo(categoryId);
        }
        if(StrUtil.isNotEmpty(nameKeyword)){
            criteria.andNameLike('%'+nameKeyword+'%');
        }
        if(StrUtil.isNotEmpty(urlKeyword)){
            criteria.andUrlLike('%'+urlKeyword+'%');
        }
        return resourceMapper.selectByExample(example);
    }

    @Override
    public List<UmsResource> listAll() {
        return resourceMapper.selectByExample(new UmsResourceExample());
    }

    /**
     * 该方法用于初始化资源与角色的映射关系。首先查询所有资源、角色及它们的关联关系，
     * 然后为每个资源构建对应的角色ID_名称列表，并将结果存入Redis中，键名为[RESOURCE_ROLES_MAP_KEY]
     * @return
     */
    @Override
    public Map<String,List<String>> initResourceRolesMap() {
        // 创建资源角色映射关系Map，使用TreeMap保证键值有序
        Map<String,List<String>> resourceRoleMap = new TreeMap<>();
        // 查询所有资源信息
        List<UmsResource> resourceList = resourceMapper.selectByExample(new UmsResourceExample());
        // 查询所有角色信息
        List<UmsRole> roleList = roleMapper.selectByExample(new UmsRoleExample());
        // 查询所有角色资源关联关系
        List<UmsRoleResourceRelation> relationList = roleResourceRelationMapper.selectByExample(new UmsRoleResourceRelationExample());

        // 遍历所有资源，构建资源与角色的映射关系
        for (UmsResource resource : resourceList) {
            // 根据资源ID筛选出关联的角色ID集合
            Set<Long> roleIds = relationList.stream().filter(item -> item.getResourceId().equals(resource.getId())).map(UmsRoleResourceRelation::getRoleId).collect(Collectors.toSet());
            // 根据角色ID集合获取对应的角色名称列表，格式为"角色ID_角色名称"
            List<String> roleNames = roleList.stream().filter(item -> roleIds.contains(item.getId())).map(item -> item.getId() + "_" + item.getName()).collect(Collectors.toList());
            // 将资源URL作为键，角色名称列表作为值存入映射关系Map中
            resourceRoleMap.put("/"+applicationName+resource.getUrl(),roleNames);
        }

        // 删除Redis中旧的资源角色映射关系
        redisService.del(AuthConstant.RESOURCE_ROLES_MAP_KEY);
        // 将新的资源角色映射关系批量存储到Redis中
        redisService.hSetAll(AuthConstant.RESOURCE_ROLES_MAP_KEY, resourceRoleMap);
        // 返回构建好的资源角色映射关系Map
        return resourceRoleMap;


    }
}

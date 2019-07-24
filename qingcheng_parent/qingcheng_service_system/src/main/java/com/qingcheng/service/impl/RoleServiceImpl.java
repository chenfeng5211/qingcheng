package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.RoleAndResourceMapper;
import com.qingcheng.dao.RoleMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.system.Role;
import com.qingcheng.pojo.system.RoleAndResource;
import com.qingcheng.pojo.system.RoleResourceCombination;
import com.qingcheng.service.system.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleAndResourceMapper roleAndResourceMapper;

    /**
     * 返回全部记录
     * @return
     */
    public List<Role> findAll() {
        return roleMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Role> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<Role> roles = (Page<Role>) roleMapper.selectAll();
        return new PageResult<Role>(roles.getTotal(),roles.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<Role> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return roleMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Role> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<Role> roles = (Page<Role>) roleMapper.selectByExample(example);
        return new PageResult<Role>(roles.getTotal(),roles.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public Role findById(Integer id) {
        return roleMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     * @param role
     */
    public void add(Role role) {
        roleMapper.insert(role);
    }

    /**
     * 修改
     * @param role
     */
    public void update(Role role) {
        roleMapper.updateByPrimaryKeySelective(role);
    }

    /**
     *  删除
     * @param id
     */
    public void delete(Integer id) {
        roleMapper.deleteByPrimaryKey(id);
    }


    /**
     * 功能描述:
     *
     * 修改角色权限
     */
    public void updateRoleResource(RoleResourceCombination roleResourceCombination) {
//        获取角色id
        Integer roleId = roleResourceCombination.getRole().getId();

//        删除用户已用的权限
        RoleAndResource roleAndResource = new RoleAndResource();
        roleAndResource.setRoleId(roleId);
        roleAndResourceMapper.delete(roleAndResource);

//        添加权限
        List<Integer> resourceIds = roleResourceCombination.getResourceIds();
//        循环插入
        for (Integer resourceId : resourceIds) {
            roleAndResource.setResourceId(resourceId);
            roleAndResourceMapper.insertSelective(roleAndResource);
        }


    }


    /**
     * 功能描述:
     * 根据角色id查询权限
     */

    public List<Integer> findRoleResource(Integer roleId) {

//        组装查询条件
        RoleAndResource roleAndResource = new RoleAndResource();
        roleAndResource.setRoleId(roleId);
        List<RoleAndResource> resourceList = roleAndResourceMapper.select(roleAndResource);

//        遍历查询到的权限角色集合，获取权限list
        List<Integer> resources = new ArrayList<Integer>();
        for (RoleAndResource resource : resourceList) {
            resources.add(resource.getResourceId());
        }
        return resources;
    }


    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Role.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 角色名称
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
            }

            // ID
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }

        }
        return example;
    }

}

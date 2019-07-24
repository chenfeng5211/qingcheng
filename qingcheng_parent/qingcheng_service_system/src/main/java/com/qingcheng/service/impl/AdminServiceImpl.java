package com.qingcheng.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.AdminAndRoleMapper;
import com.qingcheng.dao.AdminMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.system.Admin;
import com.qingcheng.pojo.system.AdminAndRole;
import com.qingcheng.pojo.system.AdminRoleCombination;
import com.qingcheng.service.system.AdminService;
import com.qingcheng.util.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = AdminService.class)
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private AdminAndRoleMapper adminAndRoleMapper;

    /**
     * 返回全部记录
     * @return
     */
    public List<Admin> findAll() {
        return adminMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Admin> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<Admin> admins = (Page<Admin>) adminMapper.selectAll();
        return new PageResult<Admin>(admins.getTotal(),admins.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<Admin> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return adminMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Admin> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<Admin> admins = (Page<Admin>) adminMapper.selectByExample(example);
        return new PageResult<Admin>(admins.getTotal(),admins.getResult());
    }

    /**
     * 根据Id查询
     * 查询当前用户及其具有的角色
     * @param id
     * @return
     */
    public Admin findById(Integer id) {

        return adminMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     * @param admin
     */
    public void add(Admin admin) {
        adminMapper.insert(admin);
    }

    /**
     * 修改
     * @param admin
     */
    public void update(Admin admin) {
        adminMapper.updateByPrimaryKeySelective(admin);
    }

    /**
     *  删除
     * @param id
     */
    public void delete(Integer id) {
        adminMapper.deleteByPrimaryKey(id);
    }

    /**
     * 功能描述:
     * 修改用户密码
     */

    @Transactional
    public void updatePassword(String loginName, Map<String, Object> searchMap) {

        Admin admin = new Admin();
//        封装admin当前用户原密码是否正确
        admin.setLoginName(loginName);
        Admin one = adminMapper.selectOne(admin);
        boolean checkpw = BCrypt.checkpw((String) searchMap.get("oldPassword"), one.getPassword());
        if(!checkpw){
            throw new RuntimeException("原密码不正确");
        }


//        修改新密码
        String newPasswordBcrypt = BCrypt.hashpw((String) searchMap.get("newPassword"), BCrypt.gensalt());

        admin.setPassword(newPasswordBcrypt);
        Example example = new Example(Admin.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("loginName", loginName);
        adminMapper.updateByExampleSelective(admin, example);

    }


    /**
     * 功能描述:
     *
     * 添加管理员和其角色
     */

    @Transactional
    public void addAdminRole(AdminRoleCombination adminRoleCombination) {
        Admin admin = adminRoleCombination.getAdmin();
        List<Integer> roleIds = adminRoleCombination.getRoleId();

        if(!StringUtils.isEmpty(admin.getPassword())) {
//        密码加密
            String gensalt = BCrypt.gensalt();
            String hashpw = BCrypt.hashpw(admin.getPassword(), gensalt);
            admin.setPassword(hashpw);
        }

            adminMapper.insertSelective(admin);

//        循环遍历角色集合，并插入数据库中
        for (int roleId : roleIds) {

            AdminAndRole adminAndRole = new AdminAndRole();
            adminAndRole.setAdminId(admin.getId());
            adminAndRole.setRoleId(roleId);
            adminAndRoleMapper.insertSelective(adminAndRole);
        }

    }

//    根据用户id查询用户和角色
    public AdminRoleCombination findAdminWithRole(Integer adminId) {

//        查询admin对象
        Admin admin = adminMapper.selectByPrimaryKey(adminId);
        admin.setPassword(null);
        List<Integer> roleIds = new ArrayList<Integer>();

        Example example = new Example(AdminAndRole.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("adminId", adminId);
        List<AdminAndRole> adminAndRoles = adminAndRoleMapper.selectByExample(example);

//        循环将角色id属性添加到集合中
        for (AdminAndRole adminAndRole : adminAndRoles) {
            roleIds.add(adminAndRole.getRoleId());
        }

//        封装到组合实体类中
        AdminRoleCombination adminRoleCombination = new AdminRoleCombination();
        adminRoleCombination.setAdmin(admin);
        adminRoleCombination.setRoleId(roleIds);

        return adminRoleCombination;
    }


    /**
     * 功能描述:
     * 修改管理员信息
     */

    @Transactional
    public void updateAdminRole(AdminRoleCombination adminRoleCombination) {

        if(adminRoleCombination != null){
            Admin admin = adminRoleCombination.getAdmin();
            if(!StringUtils.isEmpty(admin.getPassword())) {
//            密码加密
                String hashpw = BCrypt.hashpw(admin.getPassword(), BCrypt.gensalt());
                admin.setPassword(hashpw);
            }
            adminMapper.updateByPrimaryKey(admin);
//            删除中间表关联的管理员和角色
            Example example = new Example(AdminAndRole.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("adminId", admin.getId());
            int it = adminAndRoleMapper.deleteByExample(example);

//            重新添加管理员和角色关联
            for (Integer roleId : adminRoleCombination.getRoleId()) {

                AdminAndRole adminAndRole = new AdminAndRole();
                adminAndRole.setAdminId(admin.getId());
                adminAndRole.setRoleId(roleId);
                adminAndRoleMapper.insertSelective(adminAndRole);
            }



        }
    }

    /**
     * 功能描述:
     *
     * 根据登录名查询权限
     */

    public List<String> findResKey(String loginName) {

        return adminMapper.getResKey(loginName);
    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Admin.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 用户名
            if(searchMap.get("loginName")!=null && !"".equals(searchMap.get("loginName"))){
                criteria.andEqualTo("loginName",searchMap.get("loginName"));
            }
            // 密码
            if(searchMap.get("password")!=null && !"".equals(searchMap.get("password"))){
                criteria.andLike("password","%"+searchMap.get("password")+"%");
            }
            // 状态
            if(searchMap.get("status")!=null && !"".equals(searchMap.get("status"))){
                criteria.andEqualTo("status",searchMap.get("status"));
            }

            // id   ceshi
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }

        }
        return example;
    }

}

package com.qingcheng.service.system;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.system.Admin;
import com.qingcheng.pojo.system.AdminRoleCombination;

import java.util.*;

/**
 * admin业务逻辑层
 */
public interface AdminService {


    public List<Admin> findAll();


    public PageResult<Admin> findPage(int page, int size);


    public List<Admin> findList(Map<String,Object> searchMap);


    public PageResult<Admin> findPage(Map<String,Object> searchMap,int page, int size);


    public Admin findById(Integer id);

    public void add(Admin admin);


    public void update(Admin admin);


    public void delete(Integer id);

    public void updatePassword(String loginName, Map<String, Object> searchMap);

    public void addAdminRole(AdminRoleCombination adminRoleCombination);

    public AdminRoleCombination findAdminWithRole(Integer adminId);

    public void updateAdminRole(AdminRoleCombination adminRoleCombination);

    public List<String> findResKey(String loginName);
}

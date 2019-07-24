package com.qingcheng.dao;

import com.qingcheng.pojo.system.Admin;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AdminMapper extends Mapper<Admin> {


    @Select("SELECT res_key " +
            "FROM qingcheng_system.tb_resource " +
            "WHERE id " +
            "IN (" +
            "  SELECT resource_id " +
            "  FROM qingcheng_system.tb_role_resource " +
            "  WHERE role_id " +
            "  IN ( " +
            "     SELECT role_id " +
            "     FROM qingcheng_system.tb_admin_role " +
            "     WHERE admin_id = (SELECT id " +
            "        FROM qingcheng_system.tb_admin " +
            "        WHERE login_name = #{loginName} )" +
            "))")
    public List<String> getResKey(@Param("loginName") String loginName);
}

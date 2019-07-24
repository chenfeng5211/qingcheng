package com.qingcheng.dao;

import com.qingcheng.pojo.system.Menu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface MenuMapper extends Mapper<Menu> {

    @Select("SELECT  id, name, icon, url, parent_id parentId  " +
            "FROM qingcheng_system.tb_menu " +
            "WHERE id  " +
            "IN( " +
            "  SELECT menu_id  " +
            "  FROM qingcheng_system.tb_resource_menu  " +
            "  WHERE resource_id   " +
            "  IN  (" +
            "  SELECT resource_id  " +
            "    FROM qingcheng_system.tb_role_resource  " +
            "    WHERE role_id  " +
            "    IN ( " +
            "      SELECT role_id  " +
            "      FROM qingcheng_system.tb_admin_role  " +
            "      WHERE admin_id = (SELECT id  " +
            "            FROM qingcheng_system.tb_admin  " +
            "            WHERE login_name = #{loginName}) " +
            "    ) " +
            "  ) " +
            ")" +
            "UNION " +
            "SELECT id, name, icon, url, parent_id parentId  " +
            "FROM qingcheng_system.tb_menu " +
            "WHERE id " +
            "IN( " +
            " SELECT parent_id  " +
            " FROM qingcheng_system.tb_menu " +
            " WHERE id  " +
            "  IN( " +
            "  SELECT menu_id  " +
            "    FROM qingcheng_system.tb_resource_menu  " +
            "    WHERE resource_id   " +
            "    IN ( " +
            "      SELECT resource_id  " +
            "      FROM qingcheng_system.tb_role_resource  " +
            "      WHERE role_id  " +
            "      IN ( " +
            "        SELECT role_id  " +
            "        FROM qingcheng_system.tb_admin_role  " +
            "        WHERE admin_id = (SELECT id  " +
            "              FROM  qingcheng_system.tb_admin  " +
            "              WHERE login_name = #{loginName}) " +
            "     ) " +
            "   ) " +
            " ) )" +
            "UNION "+
            "SELECT id, name, icon, url, parent_id parentId " +
            "FROM qingcheng_system.tb_menu " +
            "WHERE id " +
            "IN( " +
            "  SELECT parent_id  " +
            "  FROM qingcheng_system.tb_menu " +
            "  WHERE id " +
            "  IN( " +
            "    SELECT parent_id  " +
            "    FROM tb_menu " +
            "    WHERE id  " +
            "    IN( " +
            "      SELECT menu_id  " +
            "      FROM qingcheng_system.tb_resource_menu  " +
            "      WHERE resource_id   " +
            "      IN ( " +
            "        SELECT resource_id  " +
            "        FROM qingcheng_system.tb_role_resource  " +
            "        WHERE role_id  " +
            "        IN ( " +
            "           SELECT role_id  " +
            "           FROM qingcheng_system.tb_admin_role  " +
            "           WHERE admin_id = (SELECT id  " +
            "                FROM qingcheng_system.tb_admin  " +
            "                WHERE login_name = #{loginName} ) " +
            "       ) " +
            "     ) " +
            "   ) " +
            "  )" +
            ")")
    public List<Menu> findMenuByLoginName(@Param("loginName") String loginName);
}

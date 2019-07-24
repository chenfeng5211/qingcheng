/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: AdminAndRole
 * Author:   chenf
 * Date:     2019/7/23 0023 14:32
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.pojo.system;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 〈管理员角色中间表实体类〉
 *
 * @author chenf
 * @create 2019/7/23 0023
 * @since 1.0.0
 */
@Table(name = "tb_admin_role")
public class AdminAndRole implements Serializable {

    @Id
    private Integer adminId;

    @Id
    private Integer roleId;

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
}
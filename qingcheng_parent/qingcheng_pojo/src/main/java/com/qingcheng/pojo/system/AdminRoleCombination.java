/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: AdminRoleCombination
 * Author:   chenf
 * Date:     2019/7/23 0023 14:43
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.pojo.system;

import java.io.Serializable;
import java.util.List;

/**
 * 〈管理员和用户角色组合实体类〉
 *
 * @author chenf
 * @create 2019/7/23 0023
 * @since 1.0.0
 */

public class AdminRoleCombination implements Serializable {

    private Admin admin;

    private List<Integer> roleId;


    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public List<Integer> getRoleId() {
        return roleId;
    }

    public void setRoleId(List<Integer> roleId) {
        this.roleId = roleId;
    }
}
/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: RoleAndResource
 * Author:   chenf
 * Date:     2019/7/23 0023 17:21
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.pojo.system;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 〈角色权限组合实体类〉
 *
 * @author chenf
 * @create 2019/7/23 0023
 * @since 1.0.0
 */
@Table(name = "tb_role_resource")
public class RoleAndResource implements Serializable {

    @Id
    private Integer roleId;
    @Id
    private Integer resourceId;


    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

}
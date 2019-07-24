/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: RoleResourceCombination
 * Author:   chenf
 * Date:     2019/7/23 0023 18:03
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.pojo.system;

import java.io.Serializable;
import java.util.List;

/**
 * 〈权限和角色组合实体类〉
 *
 * @author chenf
 * @create 2019/7/23 0023
 * @since 1.0.0
 */
public class RoleResourceCombination implements Serializable {

    private Role role;

    private List<Integer> resourceIds;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Integer> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(List<Integer> resourceIds) {
        this.resourceIds = resourceIds;
    }
}
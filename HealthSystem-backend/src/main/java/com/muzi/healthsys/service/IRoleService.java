package com.muzi.healthsys.service;

import com.muzi.healthsys.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author MuZi
 * @since 2023-02-23
 */
public interface IRoleService extends IService<Role> {

    boolean addRole(Role role);

    Role getRoleById(Integer id);

    void updateRole(Role role);

    void deleteRoleById(Integer id);
}

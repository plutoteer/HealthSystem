package com.muzi.healthsys.mapper;

import com.muzi.healthsys.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author MuZi
 * @since 2023-02-23
 */
public interface UserMapper extends BaseMapper<User> {
    //这个方法是用来根据用户ID查询其拥有的角色名称列表的
    public List<String> getRoleNameByUserId(Integer userId);

}

package com.itdr.mappers;

import com.itdr.pojo.Users;
import org.apache.ibatis.annotations.Param;

public interface UsersMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Users record);

    int insertSelective(Users record);

    Users selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Users record);

    int updateByPrimaryKey(Users record);

      //根据用户名密码查询用户
    Users selectByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    //用户名或邮箱是否存在
    int selectByUsernameOrEmail(@Param("str")String str, @Param("type")String type);
}
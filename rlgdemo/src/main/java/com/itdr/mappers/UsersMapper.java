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

    //根据用户名查问题
    String selecQusetionByUsername(@Param("username")String username);

    //根据用户名查询找回问题和答案
    int selectByUsernameAndQuestionAndAnswer(@Param("username")String username, @Param("question")String question, @Param("answer")String answer);

    int updateByUsernameAndPasswordnew(@Param("username")String username,@Param("passwordNew") String passwordNew);

    int selectByIdAndPasswordold(@Param("id") Integer id, @Param("passwordOld") String passwordOld);
}
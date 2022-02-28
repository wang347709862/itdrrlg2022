package com.itdr.services.impl;

import com.itdr.common.Const;
import com.itdr.common.ServerResponse;
import com.itdr.mappers.UsersMapper;
import com.itdr.pojo.Users;
import com.itdr.services.UserService;
import com.itdr.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UsersMapper usersMapper;

    //用户登录
    @Override
    public ServerResponse<Users> login(String username, String password) {
        if(username==null ||username.equals("")){
            return ServerResponse.defeatedRS("用户名不能为空");
        }

        if(password==null ||username.equals("")){
            return ServerResponse.defeatedRS("密码不能为空");
        }

        //检查用户名是否存在
        int i2 = usersMapper.selectByUsernameOrEmail(username, "username");
        if(i2<=0){
            return  ServerResponse.defeatedRS("用户名不存在");
        }

        //md5加密
        String md5password = MD5Utils.getMD5Code(password);

        //根据用户名和加密后的密码查询用户
        Users u=usersMapper.selectByUsernameAndPassword(username,md5password);
        if(u==null){
            return ServerResponse.defeatedRS("账户或密码错误");
        }

        //返回封装信息
       return ServerResponse.successRS(u);

    }


    //用户注册
    @Override
    public ServerResponse<Users> register(Users u) {
        if(u.getUsername()==null || u.getUsername().equals("")){
            return ServerResponse.defeatedRS("账户名不能为空");
        }
        if(u.getPassword()==null || u.getUsername().equals("")){
            return ServerResponse.defeatedRS("密码不能为空");
        }

        //检查邮箱等其他信息是否已被注册

        //检查用户名是否存在
        int i2 = usersMapper.selectByUsernameOrEmail(u.getUsername(), "username");
        if(i2>0){
            return  ServerResponse.defeatedRS("用户名已存在");
        }

        //md5加密
        u.setPassword(MD5Utils.getMD5Code(u.getPassword()));

        //数据库中的密码是加密的
        int i = usersMapper.insert(u);
        if(i<=0){
            return ServerResponse.defeatedRS("用户注册失败");
        }
        return ServerResponse.successRS(200,null,"用户注册成功");
    }

    //用户名或邮箱是否有效
    @Override
    public ServerResponse<Users> checkUsernameOrEmail(String str, String type) {
        if(str==null || str.equals("")){
            return ServerResponse.defeatedRS("账户名不能为空");
        }
        if(type==null || type.equals("")){
            return ServerResponse.defeatedRS("密码不能为空");
        }

        int i=usersMapper.selectByUsernameOrEmail(str,type);

        if(i>0){
            if(type.equals("username")){
                return ServerResponse.defeatedRS("用户名已存在");
            }else{
                return ServerResponse.defeatedRS("邮箱已存在");
            }
        }
        return ServerResponse.successRS(200,null,"校验成功");
    }

    //获取当前登录用户详细信息，因为session中可能不完整
    @Override
    public ServerResponse getInformation(Users user) {
        Users users = usersMapper.selectByPrimaryKey(user.getId());
        if(users==null){
            ServerResponse.defeatedRS("用户已被禁用或不存在");
        }
        users.setPassword("");
        return ServerResponse.successRS(users);
    }

    //登录状态下更改部分个人信息（不包括用户名密码等）
    @Override
    public ServerResponse updateInformation(Users u) {

        //判断邮箱不能重复
        int i2 = usersMapper.selectByUsernameOrEmail(u.getEmail(), "email");
        if(i2>0){
            return ServerResponse.defeatedRS("邮箱重复");
        }

        //这个方法采用了<if>选择片段，如果对应属性为空则不更新，有值才更新
        int i = usersMapper.updateByPrimaryKeySelective(u);
        if(i<=0){
            return ServerResponse.defeatedRS("更新个人信息失败");
        }
        Users users = usersMapper.selectByPrimaryKey(u.getId());
        return ServerResponse.successRS(Const.SUCCESS,users,"更新个人信息成功");

    }
}

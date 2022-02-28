package com.itdr.services.impl;

import com.itdr.common.Const;
import com.itdr.common.ServerResponse;
import com.itdr.mappers.UsersMapper;
import com.itdr.pojo.Users;
import com.itdr.services.UserService;
import com.itdr.utils.MD5Utils;
import com.itdr.utils.TokenCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

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

    //忘记密码，返回问题
    @Override
    public ServerResponse forgetQuestion(String username) {
        if(username==null || username.equals("")){
            return ServerResponse.defeatedRS("账户名不能为空");
        }

        //查询用户名是否存在
        int i = usersMapper.selectByUsernameOrEmail(username, Const.USERNAME);
        if(i<=0){
            return ServerResponse.defeatedRS("账户名不存在");
        }

        //根据用户名查问题
        String question=usersMapper.selecQusetionByUsername(username);
        if(question==null || question.equals("")){
            return ServerResponse.defeatedRS("未设置问题");
        }
        return ServerResponse.successRS(question);
    }

    //根据用户名查询找回问题和答案，验证是否匹配
    @Override
    public ServerResponse<Users> forgetGetQuestion(String username, String question, String answer) {

        if(username==null || username.equals("")){
            return ServerResponse.defeatedRS("未设置账户名");
        }
        if(question==null || question.equals("")){
            return ServerResponse.defeatedRS("未设置问题");
        }
        if(answer==null || answer.equals("")){
            return ServerResponse.defeatedRS("未设置答案");
        }

        //查询用户名是否存在


        //查询用户名和答案问题是否匹配
        int i=usersMapper.selectByUsernameAndQuestionAndAnswer(username,question,answer);
        if(i<=0){
            return ServerResponse.defeatedRS("问题和答案不匹配");
        }

        //全部匹配
        // 产生随机字符令牌
        String token = UUID.randomUUID().toString();
        //把令牌放入缓存中，后期用redis替代
        TokenCache.set("token_"+username,token);
        return  ServerResponse.successRS(token);
    }

    //未登录状态，根据用户名修改密码，需要回答问题后的令牌
    @Override
    public ServerResponse<Users> forgetResetPassword(String username, String passwordNew,String forgetToken) {
        if(username==null || username.equals("")){
            return ServerResponse.defeatedRS("账户名不能为空");
        }
        if(passwordNew==null || passwordNew.equals("")){
            return ServerResponse.defeatedRS("新密码不能为空");
        }
        if(forgetToken==null || forgetToken.equals("")){
            return ServerResponse.defeatedRS("非法的令牌");
        }

        //比较拿的令牌和缓存中的令牌
        String token=TokenCache.get("token_"+username);
        if(token==null || token.equals("")){
            return ServerResponse.defeatedRS("令牌过期了");
        }
        if(!token.equals(forgetToken)){
            return ServerResponse.defeatedRS("非法的令牌");
        }

        //根据用户名修改密码
        String passwordNewMD5=MD5Utils.getMD5Code(passwordNew);
        int i=usersMapper.updateByUsernameAndPasswordnew(username,passwordNewMD5);
        if(i<=0){
            return ServerResponse.defeatedRS("修改密码失败");
        }
        return ServerResponse.successRS("修改密码成功");
    }

    //登录状态，根据用户修改密码，需要验证旧密码
    @Override
    public ServerResponse<Users> resetPassword(Users user,String passwordOld, String passwordNew) {

        if(passwordOld==null || passwordOld.equals("")){
            return ServerResponse.defeatedRS("旧密码不能为空");
        }
        if(passwordNew==null || passwordNew.equals("")){
            return ServerResponse.defeatedRS("新密码不能为空");
        }

        //根据id和旧密码查询是否存在
        String passwordOldMD5=MD5Utils.getMD5Code(passwordOld);
        int i=usersMapper.selectByIdAndPasswordold(user.getId(),passwordOldMD5);
        if (i<=0) {
            return ServerResponse.defeatedRS("旧密码错误");
        }

        //修改密码
        String passwordNewMD5=MD5Utils.getMD5Code(passwordNew);
        int i2=usersMapper.updateByUsernameAndPasswordnew(user.getUsername(),passwordNewMD5);
        if (i2 <= 0) {
            return ServerResponse.defeatedRS("修改密码失败");
        }
        return ServerResponse.successRS("修改密码成功");
    }
}
